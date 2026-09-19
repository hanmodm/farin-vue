<template>
  <v-container >
    <!-- <DxDataGrid
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
    </DxDataGrid> -->
    <DxDateRangeBox
      v-if="viewRange.dateType=== 'Y'"
      v-model:value="viewRange.dates"
      ref="rangeBoxRef"
      label-mode="hidden"
      style="width:280px"
      :multi-view="false"
      :display-format="viewRange.displayFormat"/>
    <DxDateRangeBox
      v-else-if="viewRange.dateType=== 'M'"
      v-model:value="viewRange.dates"
      ref="rangeBoxRef"
      label-mode="hidden"
      style="width:280px"
      :multi-view="false"
      :display-format="viewRange.displayFormat"/>
    <DxDateRangeBox
      v-else-if="viewRange.dateType==='D'"
      v-model:value="viewRange.dates"
      ref="rangeBoxRef"
      label-mode="hidden"
      style="width:280px"
      :multi-view="false"
      :display-format="viewRange.displayFormat"/>
    <DxDateRangeBox
      v-else
      v-model:value="viewRange.dates"
      ref="rangeBoxRef"
      label-mode="hidden"
      style="width:280px"
      :multi-view="false"
      :display-format="viewRange.displayFormat"
      @update:start-date="viewRange.onStartDateChanged"
      @value-changed="viewRange.onValueChanged"/>
    <v-btn variant="outlined" @click="viewRange.onBtnClick('D')">Day</v-btn>
    <v-btn variant="outlined" @click="viewRange.onBtnClick('W')">Week</v-btn>
    <v-btn variant="outlined" @click="viewRange.onBtnClick('M')">Month</v-btn>
    <v-btn variant="outlined" @click="viewRange.onBtnClick('Y')">Year</v-btn>
  </v-container>
</template>

<script setup>
  import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
  import { DxDataGrid, DxSelection, DxEditing, DxColumn, DxPaging, DxScrolling } from "devextreme-vue/data-grid"
  import { DxDateRangeBox } from 'devextreme-vue'

  const rangeBoxRef = ref(null)
  const viewRange = reactive({
     dates: [null, null]
    ,dateType: null
    ,displayFormat: 'yyyy-MM-dd'
    ,_selectedStartDate: null
    ,onBtnClick(v) {
      viewRange.dateType = v
    }
    ,onStartDateChanged(v) {
      if (viewRange.dateType === "W") {
        let $box = document.activeElement.parentNode.parentNode.parentNode.parentNode
        if ($box.classList.contains("dx-start-datebox")) {
          viewRange._selectedStartDate = v
        }
      }
    }
    ,onValueChanged(e) {
      if (viewRange.dateType === "W") {
        viewRange.dates = [viewRange._selectedStartDate, e.value[1]]
      } else {
        viewRange.dates = [e.value[0], viewRange.alignmentDate(viewRange.dateType, e.value[1])]
      }
    }
    ,weekCell(d, idx, el) {
      if (d.view === "month") {
        if (el.classList.contains("dx-calendar-cell-start-in-row") || el.classList.contains("dx-calendar-cell-end-in-row")) {
          el.classList.add("dx-calendar-empty-cell")
        }
      }
      el.innerHTML = `<span>${d.text}</span>`
    }
    ,disabledDates(d) {
      if (d.view === "month") {
        let firstDay = d.component.option('firstDayOfWeek')*1
        return d.date.getDay() === firstDay || d.date.getDay() === ((firstDay + 6) % 7) ? false : true
      }
      return false
    }
    ,formatWeek(date) {
      if (!(date instanceof Date) || Number.isNaN(date.getTime())) return ''

      const target = new Date(date)
      const day = target.getDay() || 7
      target.setDate(target.getDate() + 4 - day)
      const yearStart = new Date(target.getFullYear(), 0, 1)
      const week = Math.ceil((((target - yearStart) / 86400000) + 1) / 7)
      return `${target.getFullYear()}-${String(week).padStart(2, '0')}W`
    }
    ,alignmentDate(t, v) {
      if (!(v instanceof Date) || Number.isNaN(v.getTime())) return v
      if (t === "M") {
        return new Date(v.getFullYear(), v.getMonth() + 1, 0)
      } else if (t === "Y") {
        return new Date(v.getFullYear(), 11, 31)
      }
      return new Date(v)
    }
  })
  watch(() => viewRange.dateType, async (v) => {
    await nextTick()
    const boxRef = rangeBoxRef.value.instance
    boxRef.option("calendarOptions.showWeekNumbers", true)
    boxRef.option("calendarOptions.firstDayOfWeek", 1)
    boxRef.option("calendarOptions.selectWeekOnClick", false)
    boxRef.resetOption("calendarOptions.cellTemplate")
    boxRef.resetOption("calendarOptions.disabledDates")

    if (v === 'Y') {
      boxRef.option("calendarOptions.minZoomLevel", "decade")
      boxRef.option("calendarOptions.maxZoomLevel", "decade")
      viewRange.displayFormat = "yyyy"
    } else if (v === 'M') {
      boxRef.option("calendarOptions.minZoomLevel", "century")
      boxRef.option("calendarOptions.maxZoomLevel", "year")
      viewRange.displayFormat = "yyyy-MM"
    } else if (v === 'D' || v === 'W') {
      boxRef.option("calendarOptions.minZoomLevel", "day")
      boxRef.option("calendarOptions.maxZoomLevel", "month")
      viewRange.displayFormat = "yyyy-MM-dd"
      if (v === 'W') {
        boxRef.option("calendarOptions.selectWeekOnClick", true)
        boxRef.option("calendarOptions.cellTemplate", viewRange.weekCell)
        boxRef.option("calendarOptions.disabledDates", viewRange.disabledDates)        
        viewRange.displayFormat = viewRange.formatWeek
      }
    }
    boxRef.repaint()
  })
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
    viewRange.dateType = "D"
  })
</script>
