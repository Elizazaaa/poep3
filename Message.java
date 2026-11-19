package poepart3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Holds all message data in parallel ArrayLists and provides the QuickChat features.
 * Index i in ids, hashes, recipients, texts and flags always describes the same message.
 */
public class Message {

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARDED = "Disregarded";
    private static final String JSON_FILE = "storedMessages.json";

    // Parallel arrays (every message)
    private final ArrayList<String> ids = new ArrayList<>();
    private final ArrayList<String> hashes = new ArrayList<>();
    private final ArrayList<String> recipients = new ArrayList<>();
    private final ArrayList<String> texts = new ArrayList<>();
    private final ArrayList<String> flags = new ArrayList<>();

    // Arrays required by Part 3
    private final ArrayList<String> sentMessages = new ArrayList<>();
    private final ArrayList<String> disregardedMessages = new ArrayList<>();
    private final ArrayList<String> storedMessages = new ArrayList<>();

    private final Random random = new Random();
    private int totalSent = 0;

    void messageManagementMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /** Simple record used to write/read one message in the JSON file. */
    private static class MessageRecord {
        String messageId;
        String messageHash;
        String recipient;
        String message;
    }

    // ---------- Validation ----------

    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    public String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("^\\+27\\d{9}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (text.length() - 250) + "; please reduce the size.";
    }

    // ---------- Creation ----------

    public String generateMessageID() {
        String id;
        do {
            id = String.valueOf(1000000000L + (long) (random.nextDouble() * 9000000000L));
        } while (ids.contains(id));
        return id;
    }

    /** First two digits of ID : message number : FIRSTWORDLASTWORD, in capitals. */
    public String createMessageHash(String id, int messageNumber, String text) {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (id.substring(0, 2) + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Handles the user's choice for a finished message (1 send, 2 disregard, 3 store). */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Adds a message to every array it belongs in. Also used to load the test data. */
    public void addMessage(String id, String recipient, String text, String flag) {
        int messageNumber = ids.size() + 1;
        ids.add(id);
        hashes.add(createMessageHash(id, messageNumber, text));
        recipients.add(recipient);
        texts.add(text);
        flags.add(flag);

        if (flag.equals(SENT)) {
            sentMessages.add(text);
            totalSent++;
        } else if (flag.equals(STORED)) {
            storedMessages.add(text);
        } else {
            disregardedMessages.add(text);
        }
    }

    public int returnTotalMessages() {
        return totalSent;
    }

    // ---------- Sending (Part 2) ----------

    /** Asks a question in a dialog; returns null if the user cancels. */
    private String ask(String question, String title) {
        return JOptionPane.showInputDialog(null, question, title, JOptionPane.QUESTION_MESSAGE);
    }

    public void sendingMessages() {
        int howMany = 0;
        while (howMany < 1) {
            String answer = ask("How many messages do you want to send?", "Send Messages");
            if (answer == null) {
                return;
            }
            try {
                howMany = Integer.parseInt(answer.trim());
                if (howMany < 1) {
                    JOptionPane.showMessageDialog(null, "Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }

        for (int i = 1; i <= howMany; i++) {
            String title = "Message " + i + " of " + howMany;
            String id = generateMessageID();
            JOptionPane.showMessageDialog(null, "Message ID generated: " + id, title, JOptionPane.INFORMATION_MESSAGE);

            String recipient;
            while (true) {
                recipient = ask("Enter recipient cell number:", title);
                if (recipient == null) {
                    return;
                }
                recipient = recipient.trim();
                String result = checkRecipientCell(recipient);
                JOptionPane.showMessageDialog(null, result);
                if (result.startsWith("Cell phone number successfully")) {
                    break;
                }
            }

            String text;
            while (true) {
                text = ask("Enter your message:", title);
                if (text == null) {
                    return;
                }
                String result = checkMessageLength(text);
                JOptionPane.showMessageDialog(null, result);
                if (result.equals("Message ready to send.") && !text.trim().isEmpty()) {
                    break;
                }
            }

            String[] options = {"Send Message", "Disregard Message", "Store Message"};
            int choice = JOptionPane.showOptionDialog(null, "What would you like to do with this message?",
                    title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) + 1;
            if (choice < 1) {
                choice = 2; // dialog closed = disregard
            }
            JOptionPane.showMessageDialog(null, SentMessage(choice));

            String flag;
            if (choice == 1) {
                flag = SENT;
            } else if (choice == 3) {
                flag = STORED;
            } else {
                flag = DISREGARDED;
            }
            addMessage(id, recipient, text, flag);

            if (flag.equals(SENT)) {
                int last = ids.size() - 1;
                JOptionPane.showMessageDialog(null,
                        "Message ID: " + ids.get(last)
                        + "\nMessage Hash: " + hashes.get(last)
                        + "\nRecipient: " + recipients.get(last)
                        + "\nMessage: " + texts.get(last),
                        "Message details", JOptionPane.INFORMATION_MESSAGE);
            }
            if (flag.equals(STORED)) {
                saveStoredToJSON();
            }
        }
        JOptionPane.showMessageDialog(null, "Total messages sent: " + returnTotalMessages());
    }

    public String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (flags.get(i).equals(SENT)) {
                sb.append("Message ID: ").append(ids.get(i))
                  .append("\nMessage Hash: ").append(hashes.get(i))
                  .append("\nRecipient: ").append(recipients.get(i))
                  .append("\nMessage: ").append(texts.get(i)).append("\n\n");
            }
        }
        return sb.toString();
    }


    public ArrayList<String> getSentMessages() { return sentMessages; }
    public ArrayList<String> getDisregardedMessages() { return disregardedMessages; }
    public ArrayList<String> getStoredMessages() { return storedMessages; }
    public ArrayList<String> getMessageHashes() { return hashes; }
    public ArrayList<String> getMessageIds() { return ids; }

    
    public String displaySenderAndRecipient(String senderName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (flags.get(i).equals(STORED)) {
                sb.append("Sender: ").append(senderName)
                  .append(" | Recipient: ").append(recipients.get(i)).append("\n");
            }
        }
        return sb.toString();
    }

    
    public String findLongestMessage() {
        String longest = "";
        for (String m : storedMessages) {
            if (m.length() > longest.length()) {
                longest = m;
            }
        }
        return longest;
    }

   
    public String searchByMessageId(String id) {
        int index = ids.indexOf(id);
        return index == -1 ? "Message ID not found." : texts.get(index);
    }

    public String getRecipientById(String id) {
        int index = ids.indexOf(id);
        return index == -1 ? "" : recipients.get(index);
    }

    /** (d) All sent or stored messages for one recipient. */
    public ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (recipients.get(i).equals(recipient) && !flags.get(i).equals(DISREGARDED)) {
                found.add(texts.get(i));
            }
        }
        return found;
    }

    /** (e) Delete using the hash, removing from every array. */
    public String deleteByHash(String hash) {
        int index = hashes.indexOf(hash.toUpperCase());
        if (index == -1) {
            return "Message hash not found.";
        }
        String text = texts.get(index);
        String flag = flags.get(index);

        if (flag.equals(SENT)) {
            sentMessages.remove(text);
            totalSent--;
        } else if (flag.equals(STORED)) {
            storedMessages.remove(text);
        } else {
            disregardedMessages.remove(text);
        }
        ids.remove(index);
        hashes.remove(index);
        recipients.remove(index);
        texts.remove(index);
        flags.remove(index);

        saveStoredToJSON();
        return "Message: \"" + text + "\" successfully deleted.";
    }

    public String getHashByIndexOfText(String text) {
        int index = texts.indexOf(text);
        return index == -1 ? "" : hashes.get(index);
    }

    /** (f) Report of all messages with the given flag (Sent for tests, Stored for the menu). */
    public String displayReport(String flag) {
        StringBuilder sb = new StringBuilder("===== " + flag + " Messages Report =====\n");
        for (int i = 0; i < ids.size(); i++) {
            if (flags.get(i).equals(flag)) {
                sb.append("Message ID: ").append(ids.get(i))
                  .append("\nMessage Hash: ").append(hashes.get(i))
                  .append("\nRecipient: ").append(recipients.get(i))
                  .append("\nMessage: ").append(texts.get(i)).append("\n\n");
            }
        }
        return sb.toString();
    }

    // ---------- JSON ----------

    /**
     * Writes all stored messages to storedMessages.json using Gson.
     * TODO: reference your source here (e.g. Gson user guide / tutorial you used).
     */
    public void saveStoredToJSON() {
        List<MessageRecord> records = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (flags.get(i).equals(STORED)) {
                MessageRecord r = new MessageRecord();
                r.messageId = ids.get(i);
                r.messageHash = hashes.get(i);
                r.recipient = recipients.get(i);
                r.message = texts.get(i);
                records.add(r);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            System.out.println("Could not save JSON file: " + e.getMessage());
        }
    }

    /** Reads the JSON file back into the arrays (skips IDs already loaded). */
    public int loadStoredFromJSON() {
        Type listType = new TypeToken<ArrayList<MessageRecord>>() { }.getType();
        int loaded = 0;
        try (FileReader reader = new FileReader(JSON_FILE)) {
            List<MessageRecord> records = new Gson().fromJson(reader, listType);
            if (records != null) {
                for (MessageRecord r : records) {
                    if (!ids.contains(r.messageId)) {
                        addMessage(r.messageId, r.recipient, r.message, STORED);
                        loaded++;
                    }
                }
            }
        } catch (IOException e) {
            // No file yet - nothing stored
        }
        return loaded;
    }

    // ---------- Test data ----------

    /** Populates the arrays with the Part 3 test data (no hard-coded arrays). */
    public void loadTestData() {
        addMessage(generateMessageID(), "+27834557896", "Did you get the cake?", SENT);
        addMessage(generateMessageID(), "+27838884567", "Where are you? You are late! I have asked you to be on time.", STORED);
        addMessage(generateMessageID(), "+27834484567", "Yohoooo, I am at your gate.", DISREGARDED);
        addMessage("0838884567", "0838884567", "It is dinner time!", SENT);
        addMessage(generateMessageID(), "+27838884567", "Ok, I am leaving without you.", STORED);
    }

    // ---------- Stored Messages menu (option 4) ----------

    public void storedMessagesMenu(String senderName) {
        String menu = "===== Stored Messages =====\n"
                + "1. Display sender and recipient of all stored messages\n"
                + "2. Display the longest stored message\n"
                + "3. Search for a message ID\n"
                + "4. Search messages for a recipient\n"
                + "5. Delete a message using its hash\n"
                + "6. Display report of stored messages\n"
                + "7. Back to main menu";
        while (true) {
            String option = ask(menu, "Stored Messages");
            if (option == null) {
                return;
            }
            switch (option.trim()) {
                case "1" -> JOptionPane.showMessageDialog(null, displaySenderAndRecipient(senderName));
                case "2" -> JOptionPane.showMessageDialog(null, "Longest message: " + findLongestMessage());
                case "3" -> {
                    String id = ask("Enter message ID:", "Search");
                    if (id != null) {
                        JOptionPane.showMessageDialog(null, "Recipient: " + getRecipientById(id.trim())
                                + "\nMessage: " + searchByMessageId(id.trim()));
                    }
                }
                case "4" -> {
                    String number = ask("Enter recipient number:", "Search");
                    if (number != null) {
                        JOptionPane.showMessageDialog(null,
                                String.join("\n", searchByRecipient(number.trim())));
                    }
                }
                case "5" -> {
                    String hash = ask("Enter message hash:", "Delete");
                    if (hash != null) {
                        JOptionPane.showMessageDialog(null, deleteByHash(hash.trim()));
                    }
                }
                case "6" -> JOptionPane.showMessageDialog(null, displayReport(STORED));
                case "7" -> { return; }
                default -> JOptionPane.showMessageDialog(null, "Invalid choice.");
            }
        }
    }
}