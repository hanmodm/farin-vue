<template>
  <v-container >
    <v-btn variant="outlined" @click="addRow">추가</v-btn>
    <v-btn variant="outlined" @click="modify">수정</v-btn>
    <DxDataGrid
        ref="gridRef"
        key-expr="ID"
        width="100%"
        :data-source="gridRows"
        :column-resizing-mode="true"
        :allow-column-reordering="true"
        :row-alternation-enabled="true"
        :show-borders="true"
        @editor-prepared="onEditPrepared">
        <DxEditing :allow-updating="true" mode="row"/>
        <DxSelection mode="single"/>
        <DxColumn data-field="ID" caption="아이디" alignment="left" width="150px"/>
        <DxColumn data-field="NAME" caption="명칭" alignment="left"/>
        <DxColumn data-field="STATE" caption="상태" alignment="center">
          <DxLookup
            :data-source="stateList"
            value-expr="CODE"
            display-expr="NAME"/>
        </DxColumn>
        <DxColumn data-field="ACTUAL_DT" caption="수행일시" data-type="date" format="yyyy-MM-dd HH:mm:ss" width="200px"/>
        <DxColumn data-field="DESC" caption="비고" alignment="left"/>
        <DxColumn type="buttons" :visible="false" />
        <DxPaging :enabled="false"/>
        <DxScrolling :mode="'virtual'"/>
    </DxDataGrid>
  </v-container>
</template>

<script setup>
  import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
  import { DxDataGrid, DxSelection, DxEditing, DxColumn, DxLookup, DxPaging, DxScrolling } from "devextreme-vue/data-grid"

  const stateList = [
     { CODE: "C", NAME: "Create" }
    ,{ CODE: "D", NAME: "Draw" }
    ,{ CODE: "A", NAME: "Absoulte" }
  ]
  const gridRef = ref(null)
  const gridRows = ref([
     { ID: "TEST0001", NAME: "테스트0001", STATE: "C", ACTUAL_DT: parseDateTime('20260901012322'), DESC: "테스트 상세 내용 0001" }
    ,{ ID: "TEST0002", NAME: "테스트0002", STATE: "D", ACTUAL_DT: parseDateTime('20260903082342'), DESC: "테스트 상세 내용 0002" }
    ,{ ID: "TEST0003", NAME: "테스트0003", STATE: "D", ACTUAL_DT: parseDateTime('20260904112322'), DESC: "테스트 상세 내용 0003" }
    ,{ ID: "TEST0004", NAME: "테스트0004", STATE: "C", ACTUAL_DT: parseDateTime('20260905212332'), DESC: "테스트 상세 내용 0004" }
    ,{ ID: "TEST0005", NAME: "테스트0005", STATE: "C", ACTUAL_DT: parseDateTime('20260906112322'), DESC: "테스트 상세 내용 0005" }
    ,{ ID: "TEST0006", NAME: "테스트0006", STATE: "C", ACTUAL_DT: parseDateTime('20260907200322'), DESC: "테스트 상세 내용 0006" }
    ,{ ID: "TEST0007", NAME: "테스트0007", STATE: "D", ACTUAL_DT: parseDateTime('20260908112322'), DESC: "테스트 상세 내용 0007" }
    ,{ ID: "TEST0008", NAME: "테스트0008", STATE: "D", ACTUAL_DT: parseDateTime('20260909112322'), DESC: "테스트 상세 내용 0008" }
    ,{ ID: "TEST0009", NAME: "테스트0009", STATE: "C", ACTUAL_DT: parseDateTime('20260909112322'), DESC: "테스트 상세 내용 0009" }
    ,{ ID: "TEST0010", NAME: "테스트0010", STATE: "A", ACTUAL_DT: parseDateTime('20260910112322'), DESC: "테스트 상세 내용 0010" }
    ,{ ID: "TEST0011", NAME: "테스트0011", STATE: "A", ACTUAL_DT: parseDateTime('20260911112322'), DESC: "테스트 상세 내용 0011" }
    ,{ ID: "TEST0012", NAME: "테스트0012", STATE: "C", ACTUAL_DT: parseDateTime('20260911112322'), DESC: "테스트 상세 내용 0012" }
  ])
  const onEditPrepared = (e) => {
    if (e.dataField === "ID") {
      e.cancel = true
      e.editorElement.parentElement.classList.remove("dx-editor-cell")
      e.editorElement.parentElement.innerHTML = e.value
    } else {
      const $editorWrapper = e.editorElement.classList.contains("dx-dropdowneditor") ? e.editorElement.children[0].children[1].children[0]
        : e.editorElement.children[0].children[0]
        
      if (!!$editorWrapper) {
        const $editor = $editorWrapper.children[0]
        $editor.style["height"] = "32px"
        $editor.style["border"] = "1px solid #d7d7d7"
        $editor.style["margin"] = "3px 10px"
        $editor.style["minWidth"] = "80px"
      }
    }
  }

  const addRow = () => {
    const grid = gridRef.value?.instance;
    grid.addRow()
  }
  const modify = () => {
    const grid = gridRef.value?.instance;
    const keys = grid?.getSelectedRowKeys()
    if (!keys.length) return
    grid.editRow(grid?.getRowIndexByKey(keys[0]))
  }

  function parseDateTime(value) {
    if (!/^\d{14}$/.test(value)) {
      throw new Error("날짜 형식은 yyyyMMddHHmmss여야 합니다.");
    }

    const year = Number(value.slice(0, 4));
    const month = Number(value.slice(4, 6));
    const day = Number(value.slice(6, 8));
    const hour = Number(value.slice(8, 10));
    const minute = Number(value.slice(10, 12));
    const second = Number(value.slice(12, 14));

    const date = new Date(year, month - 1, day, hour, minute, second);

    // 잘못된 날짜(예: 20260230120000)가 자동 보정되는 것 방지
    if (
      date.getFullYear() !== year ||
      date.getMonth() !== month - 1 ||
      date.getDate() !== day ||
      date.getHours() !== hour ||
      date.getMinutes() !== minute ||
      date.getSeconds() !== second
    ) {
      throw new Error("유효하지 않은 날짜입니다.");
    }

    return date;
  }
  /* ====================================================
   * 이벤트 선언부
   * ==================================================== */
  onMounted(() => {
    
  })
</script>

<style lang="css" scoped>
table tr td.dx-editor-cell { background-color:#e6ae77}
</style>
