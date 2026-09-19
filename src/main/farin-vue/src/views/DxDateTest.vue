<template>
  <v-container >
    <v-row align="center" no-gutters>
      <!-- DateRangeBox -->
      <v-col>
        <!-- 옵션 변경에 따라 컴포넌트 reload 처리를 강제해야 기능이 안정적으로 작동함. 제공하는 메쏘드 호출(repaint 등)로는 이벤트 오동작 가능성이 높음 -->
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
      </v-col>
      <!-- DateBox -->
      <v-col>
        <DxDateBox
          v-if="viewDate.dateType==='M' || viewDate.dateType==='Y' || viewDate.dateType==='D'"
          v-model:value="viewDate.date"
          ref="dateBoxRef"
          label-mode="hidden"
          style="width:180px"
          :display-format="viewDate.displayFormat"
          @value-changed="viewDate.onValueChanged" />
        <DxDateBox
          v-else
          v-model:value="viewDate.date"
          ref="dateBoxRef"
          label-mode="hidden"
          style="width:180px"
          type="date"
          picker-type="calendar"
          :input-attr="{ 'aria-label': 'Picker' }"
          :display-format="viewDate.displayFormat"
          @value-changed="viewDate.onValueChanged" />
        <v-btn variant="outlined" @click="viewDate.onBtnClick('D')">Day</v-btn>
        <v-btn variant="outlined" @click="viewDate.onBtnClick('W')">Week</v-btn>
        <v-btn variant="outlined" @click="viewDate.onBtnClick('M')">Month</v-btn>
        <v-btn variant="outlined" @click="viewDate.onBtnClick('Y')">Year</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
  import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
  import { DxDateRangeBox } from 'devextreme-vue'
  import { DxDateBox } from 'devextreme-vue'
  
  /* ====================================================
   * DxDateRangeBox 반응형 선언부
   * ==================================================== */
  const rangeBoxRef = ref(null)
  const viewRange = reactive({
     dates: [null, null]
    ,dateType: null
    ,displayFormat: 'yyyy-MM-dd'
    ,_selectedStartDate: null
    ,onBtnClick(v) {
      viewRange.dateType = v
      viewRange.dates[0] = weekUtil.alignmentDate(v, viewRange.dates[0], 1)
      viewRange.dates[1] = weekUtil.alignmentDate(v, viewRange.dates[1])
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
        viewRange.dates = [e.value[0], weekUtil.alignmentDate(viewRange.dateType, e.value[1])]
      }
    }
    ,cell(d, idx, el) {
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
        boxRef.option("calendarOptions.cellTemplate", viewRange.cell)
        boxRef.option("calendarOptions.disabledDates", viewRange.disabledDates)        
        viewRange.displayFormat = weekUtil.formatWeek
      }
    }
    boxRef.repaint()
  })
  /* ====================================================
   * DxDateBox 반응형 선언부
   * ==================================================== */
  const dateBoxRef = ref(null)
  const viewDate = reactive({
     date: null
    ,dateType: null
    ,displayFormat: 'yyyy-MM-dd'
    ,onBtnClick(v) {
      viewDate.dateType = v
      viewDate.date = weekUtil.alignmentDate(v, viewDate.date, 1)
    }
    ,onValueChanged(e) {
      viewDate.date = weekUtil.alignmentDate(viewDate.dateType, e.value, 1)
    }
    ,cell(d, idx, el) {
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
  })

  watch(() => viewDate.dateType, async (v) => {
    await nextTick()
    const boxRef = dateBoxRef.value.instance
    boxRef.option("calendarOptions.showWeekNumbers", true)
    boxRef.option("calendarOptions.firstDayOfWeek", 1)

    if (v === 'Y') {
      boxRef.option("calendarOptions.minZoomLevel", "decade")
      boxRef.option("calendarOptions.maxZoomLevel", "decade")
      viewDate.displayFormat = "yyyy"
    } else if (v === 'M') {
      boxRef.option("calendarOptions.minZoomLevel", "century")
      boxRef.option("calendarOptions.maxZoomLevel", "year")
      viewDate.displayFormat = "yyyy-MM"
    } else if (v === 'D' || v === 'W') {
      boxRef.option("calendarOptions.minZoomLevel", "day")
      boxRef.option("calendarOptions.maxZoomLevel", "month")
      viewDate.displayFormat = "yyyy-MM-dd"
      if (v === 'W') {
        viewDate.displayFormat = weekUtil.formatWeek
      }
    }
    boxRef.repaint()
  })
  /* ====================================================
   * 이벤트 선언부
   * ==================================================== */
  onMounted(() => {
    viewRange.dateType = "D"
    viewDate.dateType = "D"
  })
  /* ====================================================
   * 메쏘드 선언부
   * ==================================================== */
  /**
   * calendar option의 selectionMode 값은 최초 정의한 설정 값만 허용하며 사용자 변경 시 의도되지 않는 동작을 일으킬 수 있음.
   * calendar option은 컴포넌트 생성 후 시점에서 selectionMode를 제외한 변경 가능한 option 값은 동적으로 적용 가능함.
   * 때문에 컴포넌트 속성으로 전달하지 않고 watch로 key 요소 값 변경 시점에 동적으로 적용가능한 option은 동적으로 적용하는 것이 가장 안정적으로 작동
   * (그 외 옵션 변경 시 작동 오류 발생 가능)
   */
  function setDateOptions(v, refProxy, dataProxy) {
    const boxRef = refProxy.value.instance
    boxRef.option("calendarOptions.showWeekNumbers", true)
    boxRef.option("calendarOptions.firstDayOfWeek", 1)
    boxRef.option("calendarOptions.selectWeekOnClick", false)
    boxRef.resetOption("calendarOptions.cellTemplate")
    boxRef.resetOption("calendarOptions.disabledDates")

    if (v === 'Y') {
      boxRef.option("calendarOptions.minZoomLevel", "decade")
      boxRef.option("calendarOptions.maxZoomLevel", "decade")
      dataProxy.displayFormat = "yyyy"
    } else if (v === 'M') {
      boxRef.option("calendarOptions.minZoomLevel", "century")
      boxRef.option("calendarOptions.maxZoomLevel", "year")
      dataProxy.displayFormat = "yyyy-MM"
    } else if (v === 'D' || v === 'W') {
      boxRef.option("calendarOptions.minZoomLevel", "day")
      boxRef.option("calendarOptions.maxZoomLevel", "month")
      dataProxy.displayFormat = "yyyy-MM-dd"
      if (v === 'W') {
        boxRef.option("calendarOptions.selectWeekOnClick", true)
        boxRef.option("calendarOptions.cellTemplate", dataProxy.cell)
        boxRef.option("calendarOptions.disabledDates", dataProxy.disabledDates)        
        dataProxy.displayFormat = weekUtil.formatWeek
      }
    }
    boxRef.repaint()
  }

  const weekUtil = {
    formatWeek(date) {
      if (!(date instanceof Date) || Number.isNaN(date.getTime())) return ''

      const target = new Date(date)
      const day = target.getDay() || 7
      target.setDate(target.getDate() + 4 - day)
      const yearStart = new Date(target.getFullYear(), 0, 1)
      const week = Math.ceil((((target - yearStart) / 86400000) + 1) / 7)
      return `${target.getFullYear()}-${String(week).padStart(2, '0')}W`
    }
    ,alignmentDate(t, v, isStart) {
      if (!(v instanceof Date) || Number.isNaN(v.getTime())) return v
      if (t === "M") {
        return isStart ? new Date(v.getFullYear(), v.getMonth(), 1) : new Date(v.getFullYear(), v.getMonth() + 1, 0)
      } else if (t === "Y") {
        return isStart ? new Date(v.getFullYear(), 0, 1) : new Date(v.getFullYear(), 11, 31)
      }
      return new Date(v)
    }
  }
</script>
