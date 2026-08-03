
const { WebSocketServer } = require('ws')

function wsServer() {
  const wss = new WebSocketServer({ port: 9102 })
  const clients = {}
  let callPageAddr = ""
  const TIME_ZONE = 3240 * 10000

  const getDateTime = () => {
    let now = new Date()
    return new Date(+now + TIME_ZONE).toISOString().replace('T', ' ').replace(/\..*/, '')
  }

  wss.on('connection', (ws, req) => {
    let headers = req.headers
    let rawIp = headers["x-forwarded-for"]?.split(",")[0] || headers["origin"].replace(/^https?:\/\/([^/]+).*$/, '$1').split(":")[0] || req.socket.remotedAddress
    let port = req.socket.remotePort
    let clientAddr = `${rawIp}:${port}`
    
    console.log(`[${getDateTime()}] ${clientAddr} - 클라이언트 연결됨`)

    if (clientAddr.indexOf("localhost")>-1 || clientAddr.indexOf("127.0.0.1")) {
      callPageAddr = clientAddr
    }

    ws.on('message', v => {
      let value = null
      try { value = JSON.parse(v) } catch(e) { value = { message: v }}
      if (!!value.to) {
        if (!clients[value.to]) {
          clients[clientAddr].send('false')
          console.log(`[${getDateTime()}] 연결되지 않은 클라이언트 입니다.: ${value.to}`)
          return
        }
        clients[value.to].send(value.message)
        console.log(`[${getDateTime()}] 받은 메시지: ${value.to} - ${value.message}`)
      } else {
        if (!clients[callPageAddr]) {
          console.log(`[${getDateTime()}] 호출 화면 정보가 없습니다.`)
          return
        }
        clients[callPageAddr]?.send(JSON.stringify({ client: clientAddr, message: value.message }))
        console.log(`[${getDateTime()}] 받은 메시지: ${clientAddr} - ${value.message}`)
      }
    })
    ws.on("close", () => {
      delete clients[clientAddr]
      console.log(`[${getDateTime()}] ${clientAddr} - 클라이언트가 접속을 해제했습니다.`)
    })
    clients[clientAddr] = ws
  })
  console.log(`[${getDateTime()}] WebSocket 서버가 9102 포트에서 실행 중입니다.`)
}

if (require.main === module) {
  wsServer()
}

module.exports = wsServer