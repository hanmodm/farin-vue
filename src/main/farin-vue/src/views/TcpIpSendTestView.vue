<script setup>
  import { ref, onUnmounted, reactive } from 'vue'
  import socketServer from '../utils/websocket'

  const { createSocket } = socketServer()
  const sockets = ref([])

  const view = reactive({
    addr: `127.0.0.1:19302`
  })

  const onConnect = async () => {
    const socket = createSocket()

    let addr = (view.addr ?? '').split(":")
    if (addr.length === 2) {
      await socket.connect(addr[0], addr[1])
      if (socket.isConnected) {
        sockets.value.push(socket)
      } else {
        alert("서버를 연결할 수 없습니다.")
      }
    }
  }

  const onSendMessage = () => {
    const socket = sockets.value?.[view.selected]
    if (socket.isConnected) socket.instance.send(view.message)
  }

  const onDisconnect = () => {
    const socket = sockets.value?.[view.selected]
    if (!!socket) {
      socket.disconnect()
      sockets.value.splice(view.selected, 1)
      view.selected = view.selected - 1 < 0 ? 0 : view.selected - 1
    }
  }

  onUnmounted(() => {
    //socketInfo.disconnect()
  })
</script>

<template>
  <v-container>
    <v-row align="center" no-gutters>
        <v-col>
          <v-text-field v-ripple label="IP:PORT 입력" v-model="view.addr"></v-text-field>
        </v-col>
        <v-col style="margin-left:10px;">
          <v-btn variant="outlined" @click="onConnect">연결</v-btn>
          <v-btn variant="outlined" style="margin-left:4px;" @click="onDisconnect">끊기</v-btn>
        </v-col>
    </v-row>
    <v-row align="center" no-gutters>
      <v-col>
        <!-- <v-text-field v-ripple label="수신처" v-model="view.client"></v-text-field>
        <v-text-field v-ripple label="전송" v-model="view.sendMessage"></v-text-field> -->
        <!-- <v-text-field v-ripple label="객체테스트" v-model="socketInfo.test.value" :disabled="!socketInfo.test.enabled" :style="{ display: socketInfo.test.visible ? '' : 'none' }"></v-text-field> -->
        <!-- <v-btn variant="outlined" @click="onSendMessage">전송</v-btn> -->
        <!-- <v-btn variant="outlined" @click="toggleTest">토글</v-btn>
        <v-btn variant="outlined" @click="toggleTest2">숨김</v-btn> -->

        <v-btn 
          v-for="(node, idx) in sockets" :key="idx"
          variant="outlined"
          :active="view.selected === idx"
          @click="(e) => view.selected = e.target.textContent*1">{{ idx }}</v-btn>
        <v-text-field v-ripple label="메세지" v-model="view.message"></v-text-field>
        <v-btn
          class="text-none text-body-large"
          color="#5865f2"
          size="small"
          variant="flat"
          @click="onSendMessage">전송</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-container>
          <ul>
            <li v-for="(node, index) in sockets" :key="index">{{ node.receivedMessage }}</li>
          </ul>
        </v-container>
      </v-col>
    </v-row>
  </v-container>

</template>
