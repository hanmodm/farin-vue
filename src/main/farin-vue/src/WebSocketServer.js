
const { WebSocketServer } = require('ws')

function wsServer() {
  const wss = new WebSocketServer({ port: 9102 })

  wss.on('connection', (ws) => {
    console.log('클라이언트 연결됨')

    ws.on('message', (message) => {
      const text = message.toString()
      console.log(`받은 메시지: ${text}`)
      ws.send(`서버 응답: ${text}`)
    })
  })
  wss.on("close", ws => {
    console.log("클라이언트가 접속을 해제했습니다.")
    console.log(ws)
  })

  console.log('WebSocket 서버가 9102 포트에서 실행 중입니다.')
}

if (require.main === module) {
  wsServer()
}

module.exports = wsServer