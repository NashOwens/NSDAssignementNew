public interface TopicMessageListener {
    void OnMessage(String fromLogin, String msgBody);
}
