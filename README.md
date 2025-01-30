# SlotMachineComponent

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> Jetpack Compose로 구현한 **슬롯머신 스타일 숫자 애니메이션 컴포넌트**입니다.  
> 
> 슬롯머신처럼 위/아래로 말려 들어가는 애니메이션을 제공합니다.

&nbsp;

---

## 데모 이미지

&nbsp;

(슬롯머신이 동작하는 모습의 GIF나 스크린샷을 첨부할 수 있다면 여기에 삽입)

&nbsp;

---

## 주요 기능

&nbsp;

- **숫자 증가/감소 애니메이션**  
  - 하나씩 숫자가 변할 때 **슬롯머신 롤링**처럼 **위/아래**로 스크롤되는 애니메이션  
- **자릿수에 맞춰 최대/최소값 설정**  
  - 예: 3자리 → 최대 999, 4자리 → 최대 9999  
- **UI 커스터마이징 가능**  
  - **숫자 폰트 크기**(`slotFontSize`), **숫자 색상**(`slotNumberColor`), **슬롯 배경 색상**(`backgroundColor`), **모양(Shape)** 등  
  - `Modifier`를 통해 **크기, 패딩, 배경 등** 자유로운 변경 가능  
- **첫 설정 숫자 자릿수에 맞춰 UI 조정**

&nbsp;
  
---

## 사용 예시

&nbsp;

아래 예시는 `MainScreen`에서 `SlotMachine`을 호출하는 코드입니다.

```kotlin
@Composable
fun MainScreen() {
    var targetNumber by remember { mutableStateOf("") }
    var animationOn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SlotMachine(
                modifier = Modifier.size(400.dp, 200.dp),
                initialNumber = 54217,            // 시작 숫자
                targetNumber = targetNumber.toIntOrNull() ?: 100, 
                animationOn = animationOn,        // true일 때 숫자 롤링 애니메이션
                animationFinish = { animationOn = false },  // 애니메이션 종료 시 콜백
                slotFontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // 목표 숫자를 입력받는 TextField
            TextField(value = targetNumber, onValueChange = { targetNumber = it })
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // "Go!" 버튼 → animationOn = true → 롤링 시작
            Button(
                onClick = { animationOn = true },
                enabled = targetNumber.isNotEmpty() && targetNumber.toIntOrNull() != null
            ) {
                Text(text = "Go!")
            }
        }
    }
}
```

- **initialNumber**: 시작 숫자
- **targetNumber**: 목표 숫자 (TextField에서 입력받은 값)
- **animationOn**: true가 되면 내부적으로 증가/감소 애니메이션 실행
- **animationFinish**: 애니메이션이 모두 끝난 뒤 실행할 콜백

&nbsp;

---

## 프로젝트 구조 예시

&nbsp;

```css
SlotMachineComponent
├── app
│   └── src
│       └── main
│           ├── java/com/loyalflower/slotmachinecomponent
│           │   ├── MainActivity.kt
│           │   ├── MainScreen.kt
│           │   ├── animationLogic
│           │       ├── runDecrementAnimation.kt
│           │       └── runIncrementAnimation.kt
│           │   └── slotMachineUI
│           │       ├── SlotMachine.kt
│           │       └── SlotColumn.kt
│           └── AndroidManifest.xml
├── build.gradle
└── README.md
```

- **MainActivity.kt**: 앱 실행 진입점, setContent { MainScreen() }
- **MainScreen.kt**: 예시 UI (TextField, Button, SlotMachine 배치)
- **SlotMachine.kt**: 슬롯머신 UI 로직 (자릿수 계산, Animatable 리스트 관리)
- **SlotColumn.kt**: 슬롯 1칸(자리)을 그리는 Composable
- **runIncrementAnimation.kt**: 증가 애니메이션 로직
- **runDecrementAnimation.kt**: 감소 애니메이션 로직

&nbsp;

---
