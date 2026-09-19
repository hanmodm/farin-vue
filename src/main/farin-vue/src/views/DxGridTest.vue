<template>
  <v-container >
    <DxDataGrid
        ref="gridRef"
        key-expr="ID"
        width="100%"
        :data-source="gridRows"
        :allow-column-reordering="true"
        :row-alternation-enabled="true"
        :show-borders="true"
        @editor-preparing="onEditPreparing">
        <DxEditing :allow-updating="true"/>
        <DxSelection mode="single"/>
        <DxColumn data-field="ID" caption="아이디" alignment="left" width="150px"/>
        <DxColumn data-field="NAME" caption="명칭" alignment="left"/>
        <DxColumn data-field="DESC" caption="비고" alignment="left"/>
        <DxPaging :enabled="false"/>
        <DxScrolling :mode="'virtual'"/>
    </DxDataGrid>
  </v-container>
</template>

<script setup>
  import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
  import { DxDataGrid, DxSelection, DxEditing, DxColumn, DxPaging, DxScrolling } from "devextreme-vue/data-grid"

  const gridRef = ref(null)
  const gridRows = ref([
     { ID: "TEST0001", NAME: "테스트0001", DESC: "테스트 상세 내용 0001" }
    ,{ ID: "TEST0002", NAME: "테스트0002", DESC: "테스트 상세 내용 0002" }
    ,{ ID: "TEST0003", NAME: "테스트0003", DESC: "테스트 상세 내용 0003" }
    ,{ ID: "TEST0004", NAME: "테스트0004", DESC: "테스트 상세 내용 0004" }
    ,{ ID: "TEST0005", NAME: "테스트0005", DESC: "테스트 상세 내용 0005" }
    ,{ ID: "TEST0006", NAME: "테스트0006", DESC: "테스트 상세 내용 0006" }
    ,{ ID: "TEST0007", NAME: "테스트0007", DESC: "테스트 상세 내용 0007" }
    ,{ ID: "TEST0008", NAME: "테스트0008", DESC: "테스트 상세 내용 0008" }
    ,{ ID: "TEST0009", NAME: "테스트0009", DESC: "테스트 상세 내용 0009" }
    ,{ ID: "TEST0010", NAME: "테스트0010", DESC: "테스트 상세 내용 0010" }
    ,{ ID: "TEST0011", NAME: "테스트0011", DESC: "테스트 상세 내용 0011" }
    ,{ ID: "TEST0012", NAME: "테스트0012", DESC: "테스트 상세 내용 0012" }
  ])
  const onEditPreparing = (e) => {
    if (e.dataField === "ID") {
      e.cancel = true
      e.editorElement.parentElement.classList.remove("dx-editor-cell")
      e.editorElement.parentElement.innerHTML = e.value
    } else {
      e.editorElement.parentElement.classList.add("select-editable-cell")
    }
    console.log(e.editorElement.parentNode)
  }
  /* ====================================================
   * 이벤트 선언부
   * ==================================================== */
  onMounted(() => {
    
  })
</script>
