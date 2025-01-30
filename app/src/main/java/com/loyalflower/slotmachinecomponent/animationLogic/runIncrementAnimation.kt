package com.loyalflower.slotmachinecomponent.animationLogic

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

/**
 * 증가 애니메이션 로직.
 *
 * 숫자를 [start]부터 [end]까지 1씩 증가시키면서,
 * 각 자리가 변경될 때마다 위로 롤링되는 효과를 부여한다.
 *
 * 예:  123 -> 124 -> 125 ... -> 130 ...
 *
 * - 첫 스텝: (중앙 -> 위)
 * - 중간 스텝: (아래 -> 위)
 * - 마지막 스텝: (아래 -> 중앙)
 *
 * @param start               시작 숫자
 * @param end                 목표 숫자 (start < end 인 경우)
 * @param animationDuration   각 자릿수가 바뀔 때 걸리는 애니메이션 시간(ms)
 * @param easing              애니메이션 이징(easing) 함수 (예: FastOutSlowInEasing)
 * @param digitList           각 자리의 숫자(Animatable). ex) 백의 자리, 십의 자리, 일의 자리 등
 * @param digitOffsetList     각 자리의 수직 오프셋(Animatable).
 *                            슬롯 롤링 애니메이션을 위해 위/아래로 이동
 *
 * 내부 동작:
 *  1. [temp]를 [start]로 설정, [end]까지 1씩 증가시키는 while 루프 실행
 *  2. 문자열로 변환 시, 자릿수를 맞추기 위해 "%0Nd" 형태로 format,
 *     reversed()를 통해 인덱스 0이 일의 자리가 되도록 맞춤
 *  3. oldText[i]와 newText[i]가 다르면(즉, 자릿수가 바뀌면),
 *     (1) offset 스냅 (중앙 -> 아래 -> 위 이동),
 *     (2) animateTo()로 오프셋 이동 (ex: 0→-100),
 *     (3) 숫자 교체,
 *     (4) 마지막 스텝이 아니라면 다시 아래(100f)로 스냅 후 중앙(0f) 이동
 *  4. 모든 스텝을 마치면 최종적으로 [end]를 반환
 */
@SuppressLint("DefaultLocale")
suspend fun runIncrementAnimation(
    start: Int,
    end: Int,
    animationDuration: Int = 25,
    easing: Easing = FastOutSlowInEasing,
    digitList: List<Animatable<Float, *>>,
    digitOffsetList: List<Animatable<Float, *>>
): Int {
    var temp = start
    val totalSteps = end - temp
    var stepIndex = 0

    // 자릿수를 digitList.size로 측정
    val digitCount = digitList.size

    // while문: 숫자를 end까지 1씩 증가
    while (temp < end) {
        stepIndex++
        val isFirstStep = (stepIndex == 1)
        val isLastStep = (stepIndex == totalSteps)

        // oldText, newText를 자릿수(digitCount)에 맞춰 format
        // reversed()로 인덱스 0 = 일의 자리, 1 = 십의 자리, ...
        val oldText = String.format("%0${digitCount}d", temp).reversed()
        val newText = String.format("%0${digitCount}d", temp + 1).reversed()

        // 각 자리(i)에 대해, 숫자가 달라졌으면 애니메이션
        oldText.indices.forEach { i ->
            if (oldText[i] != newText[i]) {
                // 첫 스텝이면 (중앙 -> 위),
                // 중간 스텝이면 (아래 -> 위).
                digitOffsetList[i].snapTo(if (isFirstStep) 0f else 100f)

                // 위로 이동 (0->-100)
                digitOffsetList[i].animateTo(
                    targetValue = if (isLastStep) 0f else -100f,
                    animationSpec = tween(animationDuration, easing = easing)
                )

                // 숫자 교체
                digitList[i].snapTo(newText[i].digitToInt().toFloat())

                // 마지막 스텝이 아니면 다시 아래(100f)로 스냅 후 중앙(0f) 이동
                if (!isLastStep) {
                    digitOffsetList[i].snapTo(100f)
                    digitOffsetList[i].animateTo(
                        0f,
                        animationSpec = tween(animationDuration, easing = easing)
                    )
                }
            }
        }
        // temp값 증가
        temp++
    }
    return end
}
