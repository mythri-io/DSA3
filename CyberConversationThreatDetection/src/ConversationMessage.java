public class ConversationMessage {
    public final String conversationId, messageId, sender, receiver, timestamp, message;

    public ConversationMessage(String conversationId, String messageId, String sender,
                               String receiver, String timestamp, String message) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.message = message;
    }
}
