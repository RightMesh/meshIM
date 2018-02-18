package io.left.meshim.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.protobuf.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "Messages",
        indices = { @Index(value = {"RecipientID"}), @Index(value = {"SenderID"}) },
        foreignKeys =
                {
                        @ForeignKey(entity = User.class, parentColumns = "UserID",
                                childColumns = "SenderID"),
                        @ForeignKey(entity = User.class, parentColumns = "UserID",
                                childColumns = "RecipientID")
                })
public class Message implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "MessageID")
    public int id;

    @ColumnInfo(name = "Contents")
    private String message;

    @ColumnInfo(name = "Timestamp")
    private Date date;

    @ColumnInfo(name = "SenderID")
    public int senderId;

    @Ignore
    private User sender;

    @ColumnInfo(name = "RecipientID")
    public int recipientId;

    @Ignore
    private User recipient;

    @ColumnInfo(name = "SentFromDevice")
    private boolean isMyMessage;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setIsMyMessage(boolean isMyMessage) {
        this.isMyMessage = isMyMessage;
    }

    /**
     * Simplest constructor for a new message created on a device to be sent. Starts with sender,
     * recipient, and message, then passes them along
     * to {@link Message#Message(User, User, String, boolean)}.
     *
     * @param sender user that sent the message
     * @param recipient target recipient of the message
     * @param message message contents
     */
    @Ignore
    public Message(User sender, User recipient, String message) {
        this(sender, recipient, message, false);
    }

    /**
     * Next simplest constructor, extrapolates data like database ids and the time and sends them
     * along to the biggest constructor,
     * {@link Message#Message(User, int, User, int, String, boolean, Date)}.
     *
     * @param sender user that sent the message
     * @param recipient target recipient of the message
     * @param message message contents
     * @param isMyMessage if this device's user sent the message
     */
    @Ignore
    public Message(User sender, User recipient, String message, boolean isMyMessage) {
        this(sender, sender.id, recipient, recipient.id, message, isMyMessage, new Date());
    }

    /**
     * Room constructor. NOTE: {@link Message#sender} and {@link Message#recipient} will be null
     * until they can be fetched from the database based on their UserID.
     *
     * @param senderId database id for sender
     * @param recipientId database id for recipient
     * @param message message contents
     * @param isMyMessage if this device's user sent the message
     * @param date date message was sent
     */
    public Message(int senderId, int recipientId, String message, boolean isMyMessage, Date date) {
        this(null, senderId, null, recipientId, message, isMyMessage, date);
    }

    /**
     * Main constructor - sets all values from parameters.
     *
     * @param sender user that sent the message
     * @param senderId database id for sender
     * @param recipient target recipient of the message
     * @param recipientId database id for recipient
     * @param message message contents
     * @param isMyMessage if this device's user sent the message
     * @param date date message was sent
     */
    @Ignore
    public Message(User sender, int senderId, User recipient, int recipientId, String message,
                   boolean isMyMessage, Date date) {
        this.sender = sender;
        this.senderId = senderId;
        this.recipient = recipient;
        this.recipientId = recipientId;
        this.message = message;
        this.isMyMessage = isMyMessage;
        this.date = date;
    }

    /**
     * Converts local {@link Date} to a Protobuf {@link Timestamp} for use in data payloads.
     *
     * @return formatted timestamp
     */
    public Timestamp getDateAsTimestamp() {
        if (date != null) {
            long millis = date.getTime();
            return Timestamp.newBuilder()
                    .setSeconds(millis / 1000)
                    .setNanos((int) ((millis % 1000) * 1000000))
                    .build();
        } else {
            return null;
        }
    }

    /**
     * Instantiates a {@link Message }from a {@link Parcel}.
     * <p>
     *     Generated by Android Studio.
     * </p>
     * @param in Parcel to parse.
     */
    @Ignore
    protected Message(Parcel in) {
        message = in.readString();
        date = new Date(in.readLong());
        sender = in.readParcelable(User.class.getClassLoader());
        senderId = in.readInt();
        recipient = in.readParcelable(User.class.getClassLoader());
        recipientId = in.readInt();
        isMyMessage = in.readByte() != 0;
    }

    // Required by Parcelable, created by Android Studio.
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        /**
         * {@inheritDoc}.
         */
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    /**
     * {@link Parcelable} default.
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes contents to a parcel.
     * <p>
     *     Generated by Android Studio.
     * </p>
     * @param dest parcel to write to
     * @param flags flags, I guess
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeLong(date.getTime());
        dest.writeParcelable(sender, flags);
        dest.writeInt(senderId);
        dest.writeParcelable(recipient, flags);
        dest.writeInt(recipientId);
        dest.writeByte((byte) (isMyMessage ? 1 : 0));
    }

    /**
     * Function to format the date as desired
     * @param date needing formatting
     * @return  the formatted date as a string
     */
    public static String formateDate(Date date){
        DateFormat dateFormatter;
        Date today = new Date();
        if (date.getDate()==today.getDate()){
            dateFormatter = new SimpleDateFormat("hh:mm a");
        }else if(date.getYear()==today.getYear()){
            dateFormatter = new SimpleDateFormat("MMMM dd hh:mm a");
        } else {
            dateFormatter = new SimpleDateFormat("MMMM d yyyy hh:mm a");
        }
        String dateString = dateFormatter.format(date).toString();
        return dateString;
    }
}