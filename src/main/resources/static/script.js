const sendBtn = document.getElementById("send-btn");
const userInput = document.getElementById("user-input");
const chatWindow = document.getElementById("chat-window");

sendBtn.addEventListener("click", sendMessage);
userInput.addEventListener("keypress", function (e) {
  if (e.key === "Enter" && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
});

function sendMessage() {
  const message = userInput.value.trim();
  if (!message) return;

  appendMessage("user", message);
  userInput.value = "";

  // Show typing animation
  const typingIndicator = document.createElement("div");
  typingIndicator.className = "typing-dots";
  typingIndicator.textContent = "Alter Ego is typing";
  chatWindow.appendChild(typingIndicator);
  chatWindow.scrollTop = chatWindow.scrollHeight;

  fetch("http://localhost:8080/reply", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify([{ role: "user", content: message }])
  })
    .then((res) => res.text())
    .then((data) => {
      typingIndicator.remove();
      appendMessage("bot", data);
    })
    .catch((err) => {
      typingIndicator.remove();
      appendMessage("bot", "Error: " + err.message);
    });
}

function appendMessage(role, text) {
  const msg = document.createElement("div");
  msg.className = "message " + role;
  msg.textContent = text;
  chatWindow.appendChild(msg);
  chatWindow.scrollTop = chatWindow.scrollHeight;
}
