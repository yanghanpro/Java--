package moonsnake;

import javax.swing.*;

public class MSnake {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        // 设置窗口位置和大小
        frame.setBounds(10,10,900,720);
        //无法改变大小
        frame.setResizable(false);
        // 点击叉号时关闭
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 添加画布
        frame.add(new MPanel());
        // 窗口可见
        frame.setVisible(true);

    }
}
