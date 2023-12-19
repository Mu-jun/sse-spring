const sseDataElement = document.getElementById('sse-data');
const urlParams = new URLSearchParams(window.location.search);
const seq = urlParams.get('seq');
const eventSource = new EventSource('http://localhost:4000/sse/subscribe/?seq=' + seq);
// const eventSource = new EventSource('https://localhost:4000/sse/subscribe/?seq=' + seq);

eventSource.addEventListener('connect', (e) => {
    const data = e.data;
    console.log(data);
});

// 이벤트 핸들러 등록
eventSource.addEventListener('notification', (e) => {
  const data = e.data;
  sseDataElement.innerText = new Date().toLocaleString();
  sseDataElement.innerText += '\n';
  sseDataElement.innerText += data;
  sseDataElement.innerText += '\n';
});

// 에러 핸들러 등록
eventSource.onerror = (error) => {
  console.error('EventSource error:', error);
  console.error('EventSource :', this);
  //eventSource.close();
};