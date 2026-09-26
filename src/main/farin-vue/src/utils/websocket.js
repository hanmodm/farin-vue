
export const socketServer = () => {
  const createSocket = () => {
    const socketInfo = {
      instance: null
      ,sendMessage: null
      ,test: { value: null, enabled: true, visible: true }
      ,receivedMessage: []
      ,isConnected: false
      ,connect: async (ip, port) => {
        if (socketInfo.isConnected) return
        if (!ip || !port) {
          alert("IP, PORT 정보를 입력해주세요.")
          return
        }
        const ws = new WebSocket(`ws://${ip}:${port}`)

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
      ,disconnect: () => {
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
      ,onReceivedMessage: (v) => {
        let value = null
        try { value = JSON.parse(v.data) } catch(e) { value = { message: v.data }}
        if (value === false) {
          alert("수신처 연결 정보가 존재하지 않습니다.")
          return
        }
        socketInfo.receivedMessage.push(`수신: [${value.message}]`)
        console.log(value)
      }
      ,onSendMessage: () => {
        let addr = socketInfo.client?.trim().split(":")
        if (!addr[0] || !addr[1] || !socketInfo.isConnected || (typeof(str) === "string" && (!str || !str.trim())) ) return
        let sendMessage = JSON.stringify({ to: addr[0]+":"+addr[1], message: socketInfo.sendMessage})
        socketInfo.instance.send(sendMessage)
      }
      ,onClose: () => {
        socketInfo.isConnected = false
        socketInfo.receivedMessage.push(socketInfo.server + ' 접속을 종료합니다.')
      }
      ,onError: (e) => {
        console.error("웹소켓오류: ", e)
        socketInfo.receivedMessage.push(`오류: [통신 오류 발생]`)
      }
    }
    return socketInfo
  }
  return { createSocket }
}

export default socketServer;