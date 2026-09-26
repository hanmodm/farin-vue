<script setup>
  import { ref, onUnmounted, reactive } from 'vue'
  import socketServer from '../utils/websocket'

  const { socketInfo } = socketServer()
  const sockets = []

  const view = reactive({
    addr: null
  })

  const onConnect = () => {
    if (socketInfo.isConnected) {
      alert("이미 서버 접속이 완료되었습니다.")
      return
    }
    let addr = (view.addr ?? '').split(":")
    if (addr.length === 2) {
      socketInfo.connect(addr[0], addr[1])
    }
  }
  
  const onSendMessage = () => {

  }

  const onDisconnect = () => {
    if (!!socketInfo) socketInfo.disconnect()
    
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
        <v-text-field v-ripple label="수신처" v-model="view.client"></v-text-field>
        <v-text-field v-ripple label="전송" v-model="view.sendMessage"></v-text-field>
        <!-- <v-text-field v-ripple label="객체테스트" v-model="socketInfo.test.value" :disabled="!socketInfo.test.enabled" :style="{ display: socketInfo.test.visible ? '' : 'none' }"></v-text-field> -->
        <v-btn variant="outlined" @click="onSendMessage">전송</v-btn>
        <!-- <v-btn variant="outlined" @click="toggleTest">토글</v-btn>
        <v-btn variant="outlined" @click="toggleTest2">숨김</v-btn> -->
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-container>
          <ul>
            <li v-for="(message, index) in socketInfo.onReceivedMessage" :key="index">{{ message }}</li>
          </ul>
        </v-container>
      </v-col>
    </v-row>
  </v-container>

</template>
