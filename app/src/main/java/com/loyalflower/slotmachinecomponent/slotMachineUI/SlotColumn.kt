package com.loyalflower.slotmachinecomponent.slotMachineUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * 슬롯머신의 한 칸(자리)을 표시하기 위한 Composable 함수.
 *
 * @param digit      현재 슬롯 칸에 표시될 숫자(0~9).
 * @param offsetY    슬롯 칸의 수직 위치 이동 정도 (dp).
 * @param fontSize   숫자를 표시할 폰트 크기 (sp).
 * @param backgroundColor  슬롯 칸의 배경색 (Color).
 * @param shape 슬롯 칸의 모서리 모양 (Shape).
 *
 * 해당 Composable은 RowScope를 사용하여
 * Row 안에서 [Modifier.weight]로
 * 균등 분할된 공간을 차지하도록 설계됨.
 */
@Composable
fun RowScope.SlotColumn(
    digit: Int,
    offsetY: Float,
    fontSize: TextUnit,
    textColor: Color,
    backgroundColor: Color,
    shape: Shape
) {
    // Box로 감싸서 슬롯 칸의 레이아웃을 정의
    Box(
        modifier = Modifier
            // RowScope의 weight를 사용해
            // 동일 크기로 슬롯을 분할
            .weight(1f)
            // 부모 Row의 전체 높이를 사용
            .fillMaxHeight()
            // 슬롯 칸 모서리 모양
            .clip(shape)
            // 슬롯 칸 배경색
            .background(backgroundColor)
            // y축 이동 (슬롯머신 롤링 애니메이션용)
            .offset(y = offsetY.dp),
        // Box의 내부 내용(숫자)을 중앙 배치
        contentAlignment = Alignment.Center
    ) {
        // 실제 숫자 표시
        Text(
            text = digit.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
