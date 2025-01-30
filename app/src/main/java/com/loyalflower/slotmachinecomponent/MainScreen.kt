package com.loyalflower.slotmachinecomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loyalflower.slotmachinecomponent.slotMachineUI.SlotMachine

/**
 * [SlotMachine] 컴포넌트를 사용한 예시입니다.
 *
 * 내부적으로 [targetNumber]라는 문자열 상태를 입력받고,
 * [animationOn] 상태를 통해 SlotMachine 애니메이션을 트리거합니다.
 *
 * 구성:
 * 1. [SlotMachine] : 중앙에 배치 (크기 설정은 필수 입니다)
 *    - [initialNumber] : 시작 숫자
 *    - [targetNumber] : 사용자가 입력한 숫자를 Int로 변환하여 전달
 *    - [animationOn] : true일 때 목표 숫자까지 애니메이션을 실행
 *    - [animationFinish] : 애니메이션이 끝났을 때 호출되는 콜백 함수, [animationOn]을 false로 설정
 * 2. [TextField] : 사용자에게 숫자를 입력받는 필드
 * 3. [Button] : "Go!" 버튼. 누르면 [animationOn]을 true로 변경해 애니메이션 시작
 *
 * 로직 흐름:
 *  - 사용자가 TextField에 숫자를 입력하면 [targetNumber]가 갱신
 *  - [Button]을 누르면 [animationOn] = true → SlotMachine 내의 애니메이션이 트리거됨
 *  - 애니메이션이 모두 끝나면 [animationFinish] 콜백을 통해 [animationOn]을 false로 복구
 */
@Composable
fun MainScreen() {
    // TextField에서 입력받은 목표 숫자(문자열 형태)
    var targetNumber by remember { mutableStateOf("") }
    // SlotMachine 애니메이션 on/off
    var animationOn by remember { mutableStateOf(false) }

    // 전체 화면을 차지하는 Box
    Box(modifier = Modifier.fillMaxSize()) {
        // 중앙 정렬된 Column
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 슬롯머신 컴포넌트
            SlotMachine(
                modifier = Modifier.size(400.dp, 200.dp),
                initialNumber = 100,
                // 사용자가 입력하지 않았거나 형변환 불가 시 100으로 대체
                targetNumber = targetNumber.toIntOrNull() ?: 100,
                animationOn = animationOn,
                animationFinish = { animationOn = false }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // 목표 숫자를 입력받는 TextField
            TextField(
                value = targetNumber,
                onValueChange = { targetNumber = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // "Go!" 버튼. 누르면 animationOn을 true로 하여 SlotMachine 애니메이션 트리거
            Button(
                onClick = { animationOn = true },
                enabled = targetNumber.isNotEmpty() && targetNumber.toIntOrNull() != null
            ) {
                Text(text = "Go!")
            }
        }
    }
}
