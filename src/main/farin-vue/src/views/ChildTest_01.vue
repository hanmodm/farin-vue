<template>
  <v-row align="center" no-gutters>
    <v-col>
      <v-sheet>
        <v-text-field 
            v-ripple
            v-model="view.refInputTxt"
            :label="$commUtil.getMsg('lbl.etc.data1')" />
      </v-sheet>
    </v-col>
  </v-row>
</template>

<script setup>
  import { reactive, onMounted, watch, computed } from 'vue'

  const props = defineProps({
     modelValue: Object
    ,textVal: String
    ,DS_COMMONINFO: Array
  })
  const emit = defineEmits([ "update:modelValue" ])
  const view = reactive({})
  const viewMethod = {
    init() {
      viewMethod.setAttr('inputTxt', "시작 값 입력 상태")
      innerFunc()
    }
    ,setAttr(k, v) {
      view[k] = v
      emit("update:modelValue", {...view})
    }
  }
  const refInputTxt = computed({
     get: () => view.inputTxt
    ,set: (v) => viewMethod.setAttr("inputTxt", v)
  })
  /**
   * Vue는 기본적으로 단 방향(부모 -> 자식) 인자 전달을 원칙으로 한다.(서로 꼬이면 유지보수성 및 반응형 proxy 깨짐등이 발생할 소지 있음)
   * 때문에, 변경 사항 발생 시 부모 -> 자식, 자식 -> 부모 전달의 순서를 명시적으로 정의할 필요가 있음.
   * watch를 통해 부모로 modelValue 변경 사항을 전달하고, 부모는 Object.assign으로 얕은 복사로 변경 값 반영. (이 때는 값 복사만 되는 것으로 자식의 반응 proxy는 연동 안됨)
   * 부모의 변경 사항을 자식으로 전달하기 위해 modelValue 변경도 watch로 처리함. (단, watch는 비동기이기 때문에 변경 사항의 순서를 보장하지 않음.)
   */
  /** ChildTest.vue viewMethod.onBtnClick 주석을 참조하여.... 아래 watch는 제거함
  watch(() => view, (v) => { emit("update:modelValue", v) }, { deep: true })
  watch(() => props.modelValue, (v) => { if (v) Object.assign(view, v) }, { deep: true })
   */
  watch(() => props.modelValue, (v) => { if (v) Object.assign(view, v) }, { deep: true })

  onMounted(() => {
    //props.textVal = "변경함"  // 객체가 아닌 type 변수는 readonly임!
    //props.DS_COMMONINFO[0].nm = "이름을 바꿉니다."  // 외부에서도 변경된 값 유지함
    Object.assign(view, props.modelValue)
  })

  function innerFunc() {
    console.log('Child 내부 함수 호출')
  }
  defineExpose({ init: viewMethod.init, setAttr: viewMethod.setAttr })
</script>