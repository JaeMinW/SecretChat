const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const cors = require('cors');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
    cors: {
        origin: "*", // 클라이언트가 모든 오리진에서 접근할 수 있도록 설정
        methods: ["GET", "POST"]
    }
});

app.use(cors());

app.get('/', (req, res) => {
    res.send('Hello World!');
});

io.on('connection', (socket) => {
    console.log('A user connected');

    // 사용자 이름 설정
    socket.on('set username', (username) => {
        socket.username = username;
        console.log(`Username set to ${username}`);
    });

    // 메시지 수신 및 처리
    socket.on('chat message', (msg) => {
        const date = new Date();

        // 시간 및 분을 추출
        let hours = date.getHours();
        const minutes = date.getMinutes();
        const ampm = hours >= 12 ? '오후' : '오전';

        // 12시간제로 변환
        hours = hours % 12;
        hours = hours ? hours : 12; // 0시를 12시로 변환
        const formattedTime = `${ampm} ${hours}시 ${minutes}분`;

        // 메시지와 타임스탬프를 포함한 데이터 전송
        const username = socket.username || 'Anonymous';
        const message = msg;
        console.log(`Sending data: ${JSON.stringify({ username, message, timestamp: formattedTime })}`);
        io.emit('chat message', { username, message, timestamp: formattedTime });
    });

    socket.on('disconnect', () => {
        console.log('A user disconnected');
    });

    socket.on('error', (error) => {
        console.error('Socket error:', error);
    });
});

const PORT = 4000;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
