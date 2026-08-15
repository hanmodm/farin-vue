<template>
  <v-container >
    <ChildTest01 ref="viewRef" :model-value="view" :DS_COMMONINFO="DS_COMMONINFO" @update:model-value="Object.assign(view, $event)"/>
    <v-btn variant="outlined" @click="viewMethod.onBtnClick">확인</v-btn>
  </v-container>
</template>

<script setup>
  import { reactive, ref, onMounted, computed, nextTick } from 'vue'
  import ChildTest01 from './ChildTest_01.vue'

  const DS_COMMONINFO = ref([{ id: '001', nm: '이름01' }])
  let textVal = ref("test")
  const viewRef = ref(null)
  const view = reactive({
     parentValue: "myTest"
  })
  const viewMethod = {
    async onBtnClick(e) {
      console.log(DS_COMMONINFO.value)
      console.log(`Child에서 전달된 값: ${view.inputTxt}`)
      /**
       *마지막 값("다시 또 변경")이 결국 자식에 전파된다는 보장이 없음 — 가장 중요한 버그
        nextTick(cb)는 "cb가 실행되는 시점"만 보장하지, cb 실행으로 인해 새로 발생하는 반응성 변경까지 그 안에서 끝난다는 보장은 없습니다.
        실행 흐름을 추적해보면:
        viewRef.value.init()                              // 자식 view.inputTxt 변경 → flush job A 예약
        await nextTick(() => view.inputTxt = "다시 변경")   // A가 flush된 뒤 콜백 실행 → 부모 view 변경 → flush job B 예약
        await nextTick(() => view.inputTxt = "다시 또 변경") // B가 flush된 뒤(=자식에 "다시 변경" 반영됨) 콜백 실행 → flush job C 예약
        outFunc()                                          // 여기서 함수 종료. job C는 아직 flush 안 됨!
        즉 "다시 변경"은 두 번째 await nextTick이 기다려주는 덕분에 자식까지 전파되지만, 마지막에 세팅한 "다시 또 변경"은 그걸 기다려줄 다음 await가 없어서 자식에 반영되기 전에 함수가 끝나버립니다. N번 값을 바꾸려면 N+1번째 nextTick(또는 최소한 마지막에 한 번 더 await nextTick())이 필요한데, 지금 코드는 변경 횟수만큼만 nextTick을 걸어서 마지막 변경 건이 항상 한 스텝 부족합니다.

        ```js
        await nextTick(() => view.inputTxt = "다시 또 변경")
        await nextTick()   // ← 이게 있어야 "다시 또 변경"이 자식까지 도달
        ```
        이걸 지금 빼먹었기 때문에, 실제로 화면(자식의 v-text-field)에는 "다시 또 변경"이 아니라 "다시 변경"까지만 보일 가능성이 높습니다.

        2) nextTick 기반 순서 제어는 근본 해결책이 아니라 미봉책
        주석에 "watch가 처리되는 순서를 강제할 필요가 있기에 nextTick으로 처리함"이라고 되어 있는데, 위에서 봤듯 완전히 강제되는 게 아니라 "몇 번 await 했는지"에 의존하는 취약한 방식입니다. 코드를 수정하는 사람이 상태 변경을 한 줄 추가하면서 await nextTick()을 안 넣으면 바로 깨지는 구조라, 유지보수 중 재발하기 아주 쉬운 패턴입니다. 부모/자식이 서로 다른 reactive 객체 두 개를 deep watch로 동기화하는 근본 구조를 바꾸지 않는 한 이런 타이밍 문제는 계속 따라다닙니다.
        더 안정적인 대안: 자식의 상태를 부모가 직접 바꿔야 한다면, deep watch 동기화에 의존하지 말고 자식이 setInputTxt 같은 메서드를 defineExpose로 노출해서 동기적으로 즉시 반영하는 편이 훨씬 명확하고 안전합니다.
       */

       /** 
        * 이와 같이 수정 후...
        * 
          viewRef.value.init()
          console.log(`view.init 후: ${view.inputTxt}`)
          await nextTick(() => { view.inputTxt = "다시 변경" })
          await nextTick()  // 위 지적사항에 따라 nextTick을 추가함
          console.log(`Parent에서 변경 후: ${view.inputTxt}`)
          await nextTick(() => { view.inputTxt = "다시 또 변경" })
          await nextTick()  // 위 지적사항에 따라 nextTick을 추가함
          console.log(`Parent에서 변경 후: ${view.inputTxt}`)
          //innerFunc()   // ChildTest_01 scope 내 함수이므로 외부에서는 호출 불가
          outFunc()

        * 결론: 기술적으로는 동작하지만, "가이드"로 쓰기엔 위험한 패턴
          먼저 결과부터 말하면 — 이 코드는 현재 Vue 3의 스케줄러 동작 방식상 실제로 "다시 변경", "다시 또 변경" 둘 다 자식까지 정상 전파됩니다. 하지만 그 이유가 Vue의 공개 API로 보장된 동작이 아니라 내부 구현(스케줄러 큐 처리 방식)에 의존하고 있어서, 유지보수 가이드로 채택하기엔 부적절합니다. 왜 동작하는지와 왜 위험한지를 나눠서 설명드립니다.
          왜 지금은 동작하는가
          Vue 3의 watch(flush 기본값 'pre') 스케줄러는 내부적으로 하나의 큐 배열을 순회하는데, 그 순회 조건이 for (flushIndex = 0; flushIndex < queue.length; flushIndex++)처럼 매 반복마다 배열 길이를 다시 확인합니다. 즉 큐를 처리하는 도중 새로운 watcher가 트리거되어 큐에 추가되더라도, 같은 flush 사이클(같은 microtask) 안에서 재귀적으로 다 소진(drain)될 때까지 처리합니다.
          await nextTick(() => { view.inputTxt = "다시 변경" })의 흐름을 뜯어보면:
          콜백이 실행되며 부모의 view.inputTxt를 동기적으로 변경 → 이 시점엔 currentFlushPromise가 null(직전 flush가 이미 끝난 상태)이므로, 이 변경이 새로운 flush 사이클을 예약함
          콜백 실행 자체는 즉시 끝나므로 await nextTick(cb)는 이 시점에 resolve됨 (아직 새로 예약된 flush는 실행 전)
          이어지는 await nextTick() (인자 없음)은 방금 새로 생긴 currentFlushPromise를 그대로 반환 → 이 promise가 resolve되는 시점은 "자식 watch(modelValue) 발동 → 자식 view 갱신 → 자식 watch(view) 재발동 → 부모로 emit"까지의 전체 캐스케이드가 재귀적으로 다 처리된 이후
          즉 콜백으로 변경 → 그 변경의 전파 완료를 별도 nextTick()으로 기다리는 "변경 1번당 nextTick 2번" 패턴이 실제로 캐스케이드 전체가 몇 단계를 거치든 한 번의 flush 사이클 안에서 재귀적으로 소진되기 때문에 마지막 값까지 정상적으로 자식에 반영됩니다.
          그럼에도 가이드로 권장하기 어려운 이유
          1. 공식적으로 보장된 동작이 아님
          지금 동작이 맞는 이유는 Vue 스케줄러의 내부 구현 디테일(큐 배열을 동적으로 재확인하며 재귀 drain하는 방식)에 기대고 있는 것이지, Vue 공식 문서에 "watch 콜백 하나 뒤에 nextTick 두 번이면 캐스케이드가 전부 끝난다"라고 명시된 보장이 아닙니다. 향후 Vue 마이너 버전에서 스케줄러 구현이 바뀌면(예: 배치 방식 변경, flush 기본 정책 변경 등) 조용히 깨질 수 있는 코드입니다.
          2. 재귀 depth가 늘어나면 규칙이 더 복잡해짐
          지금은 부모-자식 2단계라 "콜백 1 + 빈 nextTick 1"로 충분했지만, 컴포넌트 계층이 3단, 4단으로 깊어지거나 watch 체인이 더 복잡해지면 몇 번의 nextTick이 필요한지 예측하기 어려워집니다. "일단 2개씩 붙이면 된다"는 경험칙이 다음번엔 안 맞을 수 있습니다.
          3. 실수하기 매우 쉬운 패턴
          지금 이 수정 자체가 "직전 버전에서 nextTick 하나를 빠뜨려서 마지막 값이 유실됐던" 사례의 재발 방지책인데, 이는 역설적으로 이 패턴이 얼마나 실수하기 쉬운지를 보여줍니다. 상태 변경을 한 줄 추가할 때마다 반드시 짝을 맞춰 await nextTick()을 붙여야 하고, 코드 리뷰에서도 "누락 여부"를 매번 확인해야 하는 부담이 생깁니다. 가이드로 배포하면 팀원들이 계속 이 실수를 반복할 가능성이 높습니다.
          4. 근본 원인(양방향 deep watch 동기화 구조)은 그대로임
          이 fix는 증상(타이밍 이슈)을 우회할 뿐, "서로 다른 두 reactive 객체를 deep watch로 맞물려 동기화"하는 구조 자체는 그대로 남아있습니다. 구조가 유지되는 한 이런 종류의 타이밍 문제는 앞으로도 계속 재발할 여지가 있습니다.
        */
      viewRef.value.init()
      console.log(`view.init 후: ${view.inputTxt}`)
      viewRef.value.setAttr("inputTxt", "다시 변경")
      console.log(`Parent에서 변경 후: ${view.inputTxt}`)
      viewRef.value.setAttr("inputTxt", "다시 또 변경")
      console.log(`Parent에서 변경 후: ${view.inputTxt}`)
      //innerFunc()   // ChildTest_01 scope 내 함수이므로 외부에서는 호출 불가
      outFunc()
    }
  }

  function outFunc() {
    console.log("Parent 외부 함수")
  }
</script>