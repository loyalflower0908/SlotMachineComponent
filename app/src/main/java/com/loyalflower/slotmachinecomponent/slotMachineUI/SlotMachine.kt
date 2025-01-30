package com.loyalflower.slotmachinecomponent.slotMachineUI

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loyalflower.slotmachinecomponent.animationLogic.runDecrementAnimation
import com.loyalflower.slotmachinecomponent.animationLogic.runIncrementAnimation
import kotlin.math.pow

/**
 * 슬롯머신 스타일의 숫자 애니메이션을 보여주는 Composable.
 *
 * @param modifier           외부에서 전달받는 Modifier (크기, 패딩 등 UI 설정) !!필수로 크기는 설정해줘야 한다!!.
 * @param initialNumber      시작 숫자 (기본값 100).
 * @param targetNumber       목표 숫자. [animationOn]이 활성화되면 이 숫자까지 애니메이션됨.
 * @param animationDuration  각 자릿수가 바뀔 때 걸리는 애니메이션 시간(ms).
 * @param easing             애니메이션 이징(easing) 함수 (ex: FastOutSlowInEasing).
 * @param animationOn        true로 설정 시 [targetNumber]로 애니메이션을 수행.
 *                           애니메이션이 종료되면 [animationFinish]가 호출되고 다시 false로 만드는 식으로 사용.
 * @param animationFinish    애니메이션이 모두 끝났을 때 실행될 콜백 함수.
 * @param slotFontSize       각 슬롯 칸(자리)의 숫자 폰트 크기.
 * @param slotNumberColor    각 슬롯 칸의 숫자 색상.
 * @param backgroundColor    각 슬롯 칸의 배경 색상.
 * @param shape              각 슬롯 칸의 모양(Shape). RoundedCornerShape 등 사용 가능.
 *
 * 내부적으로 [initialNumber]의 자릿수를 기준으로
 * [currentDigitList]와 [currentDigitOffsetList]를 만들어서,
 * 각 자리의 숫자와 위치(offset)를 Animatable로 관리함.
 *
 * 이후 Row를 통해 자릿수만큼의 [SlotColumn]을 생성해 나열.
 * (오른쪽에서 왼쪽 순으로 숫자를 배치하기 위해
 *  [initialNumberSize - 1 downTo 0] 순서를 사용)
 *  -> 리스트에 일의 자리수부터 순서대로 들어가기 때문
 *
 * [LaunchedEffect(animationOn)]:
 *   - animationOn이 true가 되면 [targetNumber]를 [target] 상태에 대입,
 *     즉 “목표값 설정” 단계 수행.
 *
 * [LaunchedEffect(target)]:
 *   - [target]이 바뀔 때마다 실제 증가/감소 로직 수행.
 *   - 먼저 자릿수(digitCount)에 따른 최대값(예: 999, 9999 등)을 구해
 *     [finalTarget]으로 clamp(0~maxValue).
 *   - [finalTarget]이 [current]와 같지 않다면,
 *     - 감소(runDecrementAnimation) 또는 증가(runIncrementAnimation)
 *       함수를 호출하며 각 자릿수를 순차적으로 애니메이션.
 *   - 애니메이션 종료 후 [animationFinish] 콜백을 호출해
 *     animationOn 플래그를 false로 만드는 등 후처리 가능.
 */
@Composable
fun SlotMachine(
    modifier: Modifier = Modifier,
    initialNumber: Int = 100,
    targetNumber: Int = 100,
    animationDuration: Int = 25,
    easing: Easing = FastOutSlowInEasing,
    animationOn: Boolean = false,
    animationFinish: () -> Unit = {},
    slotFontSize: TextUnit = 48.sp,
    slotNumberColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    // 현재 표시 중인 숫자와 목표 숫자를 remember로 관리
    var current by remember { mutableIntStateOf(initialNumber) }
    var target by remember { mutableIntStateOf(initialNumber) }

    // 초기 숫자의 자릿수(예: 3이면 999까지, 4면 9999까지)
    val initialNumberSize = initialNumber.toString().length

    // 각 자리별 Animatable(Float) 생성: 숫자값(digit) & 오프셋(offset)
    val currentDigitList = remember { mutableListOf<Animatable<Float, AnimationVector1D>>() }
    for (i in 0 until initialNumberSize) {
        currentDigitList.add(
            remember {
                Animatable(
                    (current / Math.pow(10.0, i.toDouble()).toInt() % 10).toFloat()
                )
            }
        )
    }

    val currentDigitOffsetList = remember { mutableListOf<Animatable<Float, AnimationVector1D>>() }
    for (j in 0 until initialNumberSize) {
        currentDigitOffsetList.add(remember { Animatable(0f) })
    }

    // 슬롯(자리) UI를 Row로 나열
    Row(modifier = modifier) {
        // 자릿수를 거꾸로 순회해, "백의 자리 -> 십의 자리 -> 일의 자리" 순으로 배치
        for (slot in initialNumberSize - 1 downTo 0) {
            SlotColumn(
                digit = currentDigitList[slot].value.toInt(),
                offsetY = currentDigitOffsetList[slot].value,
                fontSize = slotFontSize,
                textColor = slotNumberColor,
                backgroundColor = backgroundColor,
                shape = shape
            )
        }
    }

    // animationOn이 true로 변하면 targetNumber를 받아서 실제 타겟값으로 변경
    LaunchedEffect(animationOn) {
        if (animationOn) {
            target = targetNumber
        }
    }

    // target이 바뀔 때마다 증가/감소 애니메이션 수행
    LaunchedEffect(target) {
        // 자릿수만큼 최대값 계산 (예: 3 -> 999, 4 -> 9999)
        val maxValue = (10.0.pow(initialNumberSize.toDouble()) - 1).toInt()
        // 최소 0, 최대 maxValue 범위로 보정
        val finalTarget = target.coerceAtLeast(0).coerceAtMost(maxValue)

        // 이미 같으면 애니메이션 필요 없음
        if (finalTarget == current) return@LaunchedEffect

        // 감소 or 증가 로직 분기
        current = if (finalTarget < current) {
            runDecrementAnimation(
                start = current,
                end = finalTarget,
                animationDuration = animationDuration,
                easing = easing,
                digitList = currentDigitList,
                digitOffsetList = currentDigitOffsetList
            )
        } else {
            runIncrementAnimation(
                start = current,
                end = finalTarget,
                animationDuration = animationDuration,
                easing = easing,
                digitList = currentDigitList,
                digitOffsetList = currentDigitOffsetList
            )
        }

        // 애니메이션 종료 시 실행할 콜백
        animationFinish()
    }
}
