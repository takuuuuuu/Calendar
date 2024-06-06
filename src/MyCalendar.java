import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MyCalendar extends JFrame {
    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JLabel timeLabel;
    private JPanel calendarPanel;
    private LocalDate currentDate;
    private Map<LocalDate, String> notesMap;// 表示一个键值对的集合，其中键的类型是 LocalDate，值的类型是 String。
    private JButton alarmButton;
    private LocalTime alarmTime;
    private AlarmDialogExample alarmDialog;

    public MyCalendar() {//设置窗口的标题、大小和布局
        super("日历");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        // 初始化当前日期和记事本内容的存储
        currentDate = LocalDate.now();
        notesMap = new HashMap<>();

        // 年份选择下拉列表
        yearComboBox = new JComboBox<>();
        for (int year = 1900; year <= 2100; year++) {
            yearComboBox.addItem(String.valueOf(year));
        }
        yearComboBox.setSelectedItem(String.valueOf(currentDate.getYear()));
        yearComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCalendarPanel();
            }
        });

        // 月份选择下拉列表
        monthComboBox = new JComboBox<>();
        String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        for (String month : months) {
            monthComboBox.addItem(month);
        }
        monthComboBox.setSelectedItem(months[currentDate.getMonthValue() - 1]);
        monthComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCalendarPanel();
            }
        });

        // 时间标签
        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateTimeLabel(); // 更新时间标签

        // 年份和月份选择面板
        JPanel selectionPanel = new JPanel(new FlowLayout());
        selectionPanel.add(yearComboBox);
        selectionPanel.add(monthComboBox);
        selectionPanel.add(timeLabel);
        add(selectionPanel, BorderLayout.NORTH);

        // 创建日历表格面板
        calendarPanel = new JPanel(new GridLayout(0, 7));
        updateCalendarPanel(); // 更新日历表格

        // 创建底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(calendarPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.CENTER);

        // 设置闹钟按钮
        alarmButton = new JButton("设置提醒事项");
        alarmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAlarmDialog();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(alarmButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateCalendarPanel() {//用于更新日历表格面板的内容
        calendarPanel.removeAll();

        // 添加表头
        String[] weekdays = {"一", "二", "三", "四", "五", "六", "日"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, SwingConstants.CENTER);
            calendarPanel.add(label);
        }

        // 获取选择的年份和月份
        int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
        int month = monthComboBox.getSelectedIndex() + 1;

        // 创建指定年份和月份的日历
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        // 补充前面的空白
        for (int i = 1; i < startDayOfWeek; i++) {
            calendarPanel.add(new JLabel());
        }

        // 添加日期按钮
        LocalDate date = firstDayOfMonth;
        while (date.getMonthValue() == month) {
            JButton button = new JButton(String.valueOf(date.getDayOfMonth()));
            final LocalDate finaldate = date;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showNoteEditor(finaldate);
                }
            });

            // 如果是当前实时日期，标记为红色
            if (date.equals(LocalDate.now())) {
                button.setForeground(Color.RED);
            }

            // 如果有闹钟时间设置且与当前日期匹配，标记为绿色
            if (alarmTime != null && date.equals(LocalDate.now()) && LocalTime.now().isAfter(alarmTime)) {
                button.setForeground(Color.GREEN);
            }

            calendarPanel.add(button);
            date = date.plusDays(1);
        }

        // 更新界面
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showNoteEditor(LocalDate date) {//显示记事本编辑器对话框
        String note = notesMap.get(date);
        if (note == null) {
            note = "";
        }

        JTextArea textArea = new JTextArea(note, 10, 20);//文本区域
        JScrollPane scrollPane = new JScrollPane(textArea);//滚动面板

        JPanel noteEditorPanel = new JPanel(new BorderLayout());
        noteEditorPanel.add(scrollPane, BorderLayout.CENTER);

        int option = JOptionPane.showOptionDialog(this, noteEditorPanel, "记事本", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            String updatedNote = textArea.getText();
            notesMap.put(date, updatedNote);

            JButton button = getButtonForDate(date);
            if (button != null && !updatedNote.isEmpty()) {
                button.setForeground(Color.BLUE);
            }else if (date.equals(LocalDate.now())){
                //当天日期，恢复为红色
                button.setForeground(Color.RED);
            }else {
                //删除内容后如果不是当天日期，就变为黑色
                button.setForeground(Color.BLACK);
            }

        }
    }

    private JButton getButtonForDate(LocalDate date) {//根据日期获取对应的按钮
        Component[] components = calendarPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(String.valueOf(date.getDayOfMonth()))) {
                    return button;//遍历组件，找到与指定日期对应的按钮并返回
                }
            }
        }
        return null;
    }

    private void updateTimeLabel() {//更新时间标签的内容
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalTime currentTime = LocalTime.now();
                timeLabel.setText("当前时间：" + currentTime.format(formatter));
            }
        });//创建时间格式化器和定时器。
       // 定时器每秒触发一次，获取当前时间并更新时间标签的文本
        timer.start();
    }

    private void showAlarmDialog() {//定义私有方法showAlarmDialog()，用于显示闹钟对话框
        alarmDialog = new AlarmDialogExample(); // 传入对AlarmDialogExample的引用
        alarmDialog.setVisible(true);
        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {//使用SwingUtilities.invokeLater()方法在事件调度线程中创建MyCalendar对象并设置可见性
            public void run() {
                MyCalendar app = new MyCalendar();
                app.setVisible(true);
            }
        });
    }
}
