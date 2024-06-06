import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import javax.swing.text.*;

public class AlarmDialogExample extends JFrame {
private JComboBox<Integer> monthComboBox;
private JComboBox<Integer> dayComboBox;
private JComboBox<Integer> hourComboBox;
private JComboBox<Integer> minuteComboBox;
private JTextField noteTextField;
private JTextPane reminderTextPane;
private List<Alarm> alarms;
private JPanel alarmPanel;

public AlarmDialogExample() {
        alarms = new ArrayList<>();
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new FlowLayout());

        // 月份 ComboBox
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        monthComboBox = new JComboBox<>(months);
        panel.add(monthComboBox);
        panel.add(new JLabel("月"));

        // 日 ComboBox
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
        days[i] = i + 1;
        }
        dayComboBox = new JComboBox<>(days);
        panel.add(dayComboBox);
        panel.add(new JLabel("日"));

        // 小时 ComboBox
        Integer[] hours = new Integer[24];
        for (int i = 0; i < 24; i++) {
        hours[i] = i;
        }
        hourComboBox = new JComboBox<>(hours);
        panel.add(hourComboBox);
        panel.add(new JLabel("时"));

        // 分钟 ComboBox
        Integer[] minutes = new Integer[60];
        for (int i = 0; i < 60; i++) {
        minutes[i] = i;
        }
        minuteComboBox = new JComboBox<>(minutes);
        panel.add(minuteComboBox);
        panel.add(new JLabel("分"));

        // 内容 TextField
        noteTextField = new JTextField(20);
        panel.add(new JLabel("内容："));
        panel.add(noteTextField);

        // 优先级 ComboBox
        String[] priorities = {"重要", "中等", "轻松"};
        JComboBox<String> priorityComboBox = new JComboBox<>(priorities);
        panel.add(new JLabel("优先级："));
        panel.add(priorityComboBox);

        JButton setButton = new JButton("确定");
        setButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
        setAlarm(priorityComboBox.getSelectedItem().toString());
        }
        });
        panel.add(setButton);
        add(panel, BorderLayout.NORTH);

        JPanel opanel = new JPanel();
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
        dispose(); // 关闭当前对话框
        }
        });
        opanel.add(closeButton);
        add(opanel, BorderLayout.SOUTH);

        JPanel txtpanel = new JPanel();
        reminderTextPane = new JTextPane();
        reminderTextPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reminderTextPane);
        scrollPane.setPreferredSize(new Dimension(400, 300)); // 设置首选大小
        txtpanel.add(scrollPane);
        add(txtpanel, BorderLayout.CENTER);


        add(txtpanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // 将对话框居中显示
        setVisible(true);
        }

private void setAlarm(String priority) {
        int selectedMonth = (int) monthComboBox.getSelectedItem();
        int selectedDay = (int) dayComboBox.getSelectedItem();
        int selectedHour = (int) hourComboBox.getSelectedItem();
        int selectedMinute = (int) minuteComboBox.getSelectedItem();

        LocalDateTime alarmDateTime = LocalDateTime.now()
        .withMonth(selectedMonth)
        .withDayOfMonth(selectedDay)
        .withHour(selectedHour)
        .withMinute(selectedMinute)
        .withSecond(0)
        .withNano(0);

        LocalDateTime currentDateTime = LocalDateTime.now();

        if (alarmDateTime.isBefore(currentDateTime)) {
        JOptionPane.showMessageDialog(this, "时间写错了！！！！", "错误", JOptionPane.ERROR_MESSAGE);
        return;
        }

        String note = noteTextField.getText();
        Alarm alarm = new Alarm(alarmDateTime, note, priority);
        alarms.add(alarm);

        Duration duration = Duration.between(currentDateTime, alarmDateTime);

        Timer timer = new Timer((int) duration.toMillis(), new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "动起来！\n" + alarm.getNote(), "闹钟", JOptionPane.INFORMATION_MESSAGE);
        alarms.remove(alarm);
        updateReminderTextPane();
        }
        });

        timer.setRepeats(false);
        timer.start();

        updateReminderTextPane();
        clearInputs();
        }

private void updateReminderTextPane() {
        StyledDocument document = reminderTextPane.getStyledDocument();
        StyleContext context = StyleContext.getDefaultStyleContext();
        Style defaultStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

        try {
        document.remove(0, document.getLength());

        for (Alarm alarm : alarms) {
        Style style;
        if (alarm.getPriority().equals("重要")) {
        style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(style, Color.RED);
        } else if (alarm.getPriority().equals("中等")) {
        style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(style, Color.ORANGE);
        } else {
        style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(style, Color.BLACK);
        }

        document.insertString(document.getLength(), alarm.getFormattedInfo() + "\n", style);
        }
        } catch (BadLocationException e) {
        e.printStackTrace();
        }
        }

private void clearInputs() {
        noteTextField.setText("");
        monthComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
        hourComboBox.setSelectedIndex(0);
        minuteComboBox.setSelectedIndex(0);
        }

public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
public void run() {
        new AlarmDialogExample();
        }
        });
        }

private class Alarm {
    private LocalDateTime dateTime;
    private String note;
    private String priority;

    public Alarm(LocalDateTime dateTime, String note, String priority) {
        this.dateTime = dateTime;
        this.note = note;
        this.priority = priority;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFormattedInfo() {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("MM月dd日HH时mm分"));
        return formattedDateTime + "：" + note;
    }

    public String getNote() {
        return note;
    }

    public String getPriority() {
        return priority;
    }
}}
