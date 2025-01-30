package com.loyalflower.slotmachinecomponent.animationLogic

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

/**
 * 감소 애니메이션 로직.
 *
 * 숫자를 [start]에서 [end]까지 1씩 감소시키면서,
 * 각 자리가 변경될 때마다 아래로 롤링되는 효과를 준다.
 *
 * 예:  123 -> 122 -> 121 ... -> 119 ...
 *
 * - 첫 스텝: (중앙 -> 아래)
 * - 중간 스텝: (위 -> 아래)
 * - 마지막 스텝: (위 -> 중앙)
 *
 * @param start             시작 숫자
 * @param end               목표 숫자( start > end 인 경우 )
 * @param animationDuration 각 자릿수가 바뀔 때 걸리는 애니메이션 시간(ms)
 * @param easing            애니메이션 이징(easing) 함수. (예: FastOutSlowInEasing)
 * @param digitList         각 자리(Animatable) → 숫자( ex: 7 → 8 )
 * @param digitOffsetList   각 자리(Animatable) → 위치 오프셋( ex: 중앙 → 아래 )
 *
 * 동작 과정:
 *  1. while 루프를 돌며 temp값을 1씩 감소.
 *  2. 자릿수(digitCount)만큼 문자열을 포맷 ("%0Nd")하고 reversed()로 뒤집어
 *     [oldText], [newText]를 생성.
 *     - 예: temp=100 -> oldText=\"001\" (reversed -> \"100\")
 *  3. oldText[i] != newText[i]이면 해당 자릿수가 변경되었으므로
 *     - (1) 오프셋을 스냅(중앙=0, 위=-100)
 *     - (2) 아래로 이동 (0→100)
 *     - (3) 숫자를 새 값으로 교체
 *     - (4) 마지막 스텝이 아니면 다시 위(-100f)로 스냅 후 중앙(0f)으로 애니메이트
 *  4. 모든 감소 스텝을 마치면 end 반환
 */
@SuppressLint("DefaultLocale")
suspend fun runDecrementAnimation(
    start: Int,
    end: Int,
    animationDuration: Int = 25,
    easing: Easing = FastOutSlowInEasing,
    digitList: List<Animatable<Float, *>>,      // 숫자값 애니메이션 (ex: 7 → 8)
    digitOffsetList: List<Animatable<Float, *>> // 위치 애니메이션 (ex: 중앙 → 아래)
): Int {
    var temp = start
    val totalSteps = temp - end
    var stepIndex = 0

    // 각 자릿수( ex: 3자리 -> [0..2], 4자리 -> [0..3] )
    val digitCount = digitList.size

    while (temp > end) {
        stepIndex++
        val isFirstStep = (stepIndex == 1)
        val isLastStep = (stepIndex == totalSteps)

        // 문자열 포맷 후 뒤집어서 인덱스 i=0이 일의 자리
        val oldText = String.format("%0${digitCount}d", temp).reversed()
        val newText = String.format("%0${digitCount}d", temp - 1).reversed()

        oldText.indices.forEach { i ->
            if (oldText[i] != newText[i]) {
                // 첫 스텝이면 (중앙->아래), 중간 스텝이면 (위->아래)
                digitOffsetList[i].snapTo(if (isFirstStep) 0f else -100f)
                digitOffsetList[i].animateTo(
                    targetValue = if (isLastStep) 0f else 100f,
                    animationSpec = tween(animationDuration, easing = easing)
                )

                // 숫자값 교체
                digitList[i].snapTo(newText[i].digitToInt().toFloat())

                // 마지막 스텝이 아니면 다시 위(-100f)로 스냅 후 중앙(0f)으로
                if (!isLastStep) {
                    digitOffsetList[i].snapTo(-100f)
                    digitOffsetList[i].animateTo(
                        0f,
                        animationSpec = tween(animationDuration, easing = easing)
                    )
                }
            }
        }
        temp--
    }
    return end
}