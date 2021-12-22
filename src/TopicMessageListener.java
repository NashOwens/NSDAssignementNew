public interface TopicMessageListener {
    void OnMessage(String fromLogin, String time, String msgBody);
}
