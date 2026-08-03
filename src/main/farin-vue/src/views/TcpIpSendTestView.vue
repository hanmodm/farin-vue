<script setup>
  import { ref, onUnmounted, reactive } from 'vue'

  const socketInfo = reactive({
     instance: null
    ,sendMessage: null
    ,receivedMessage: []
    ,isConnected: false
    ,async connect() {
      if (socketInfo.isConnected) return
      if (!socketInfo.server) {
        alert("IP, PORT 정보를 입력해주세요.")
        return
      }
      const ws = new WebSocket(`ws://${socketInfo.server}/socket`)

      return new Promise((resolve, reject) => {
        ws.onopen = () => {
          socketInfo.instance = ws
          socketInfo.isConnected = true
          socketInfo.instance.onmessage = socketInfo.onReceivedMessage
          socketInfo.instance.onclose = socketInfo.onClose
          socketInfo.instance.onerror = socketInfo.onError
          resolve(true)
        }
        ws.onerror=(e) => {
          console.error(e)
          socketInfo.instance = null
          socketInfo.isConnected = false
          resolve(false)
        }
      })
    }
    ,disconnect() {
      try {
        if (!!socketInfo.isConnected) {
          socketInfo.instance.close()
        }
      } catch(e) {
        console.error(e)
      } finally {
        socketInfo.isConnected = false
        socketInfo.instance = null
        socketInfo.receivedMessage = []
      }
    }
    ,onReceivedMessage(v) {
      let value = null
      try { value = JSON.parse(v.data) } catch(e) { value = { message: v.data }}
      if (value === false) {
        alert("수신처 연결 정보가 존재하지 않습니다.")
        return
      }
      socketInfo.receivedMessage.push(`수신: [${value.message}]`)
      console.log(value)
    }
    ,onSendMessage() {
      let addr = socketInfo.client?.trim().split(":")
      if (!addr[0] || !addr[1] || !socketInfo.isConnected || (typeof(str) === "string" && (!str || !str.trim())) ) return
      let sendMessage = JSON.stringify({ to: addr[0]+":"+addr[1], message: socketInfo.sendMessage})
      socketInfo.instance.send(sendMessage)
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
        <v-col>
          <v-text-field v-ripple label="IP:PORT 입력" v-model="socketInfo.server"></v-text-field>
        </v-col>
        <v-col style="margin-left:10px;">
          <v-btn variant="outlined" @click="socketInfo.connect">연결</v-btn>
          <v-btn variant="outlined" style="margin-left:4px;" @click="socketInfo.disconnect">끊기</v-btn>
        </v-col>
    </v-row>
    <v-row align="center" no-gutters>
      <v-col>
        <v-text-field v-ripple label="수신처" v-model="socketInfo.client"></v-text-field>
        <v-text-field v-ripple label="전송" v-model="socketInfo.sendMessage"></v-text-field>
        <v-btn variant="outlined" @click="socketInfo.onSendMessage">전송</v-btn>
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
