self.addEventListener('push', (event) => {
  console.log("Push Event received:", event);

  if (!event.data) {
    console.warn("No data in push event!");
    return;
  }

  let data = {};
  try {
    data = event.data.json();
  } catch (err) {
    console.error("Error parsing push data:", err);
  }

  console.log("Push Data:", data);

  const title = data.title || 'JIT Notification';
  const options = {
    body: data.body || 'Somet