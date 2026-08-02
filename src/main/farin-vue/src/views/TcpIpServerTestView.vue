<script setup>
  import { ref, onUnmounted, reactive } from 'vue'

  const socketInfo = reactive({
     instance: null
    ,sendMessage: null
    ,receivedMessage: []
    ,isConnected: false
    ,connect() {
      if (socketInfo.isConnected) return
      try {
        socketInfo.instance = new WebSocket.server({ port: 9102 })
        socketInfo.instance.onopen = socketInfo.onOpen
        socketInfo.instance.onmessage = socketInfo.onReceivedMessage
        socketInfo.instance.onclose = socketInfo.onClose
        socketInfo.instance.onerror = socketInfo.onError
      } catch(e) {
        socketInfo.disconnect()
      }
    }
    ,disconnect() {
      try {
        if (!!socketInfo.instance) {
          socketInfo.instance.close()
          socketInfo.isConnected = false
        }
      } catch(e) { }
    }
    ,onOpen() {
      socketInfo.isConneted = true
      socketInfo.receivedMessage.push(socketInfo.server + ' 접속에 성공했습니다.')
    }
    ,onReceivedMessage(e) {
      socketInfo.receivedMessage.push(`수신: [${e.data}]`)
    }
    ,onSendMessage(v) {
      if (!socketInfo.isConnected || !socketInfo.sendMessage || !socketInfo.sendMessage?.trim()) return
      socketInfo.instance.send(v.trim())
    }
    ,onClose() {
      socketInfo.isConnected = false
      socketInfo.receivedMessage.push(socketInfo.server + ' 접속을 종료합니다.')
    }
    ,onError(e) {
      console.error("웹소켓오류: ", e)
      socketInfo.receivedMessage.push(`오류: [통신 오류 발생]`)
    }
  })
  

  onUnmounted(() => {
    socketInfo.disconnect()
  })
</script>

<template>
  <v-container>
    <v-row align="center" no-gutters>
        <v-col style="margin-left:10px;">
          <v-btn variant="outlined" @click="socketInfo.connect">연결</v-btn>
          <v-btn variant="outlined" style="margin-left:4px;" @click="socketInfo.disconnect">끊기</v-btn>
        </v-col>
    </v-row>
    <v-row align="center" no-gutters>
      <v-col>
        <v-text-field v-ripple label="전송" v-model="socketInfo.sendMessage"></v-text-field>
        <v-btn variant="outlined" @click="socketInfo.onSendMessage">전송</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-container>
          <ul>
            <li v-for="(message, index) in socketInfo.receivedMessage" :key="index">{{ message }}</li>
          </ul>
        </v-container>
      </v-col>
    </v-row>
  </v-container>

</template>
