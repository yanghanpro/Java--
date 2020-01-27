package moonsnake;


import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

// 画布
public class MPanel extends JPanel implements KeyListener, ActionListener {
    // 图片
    ImageIcon title;
    ImageIcon body;
    ImageIcon up;
    ImageIcon down;
    ImageIcon left;
    ImageIcon right;
    ImageIcon food;

    // 蛇的默认长度
    int len=3;
    int score = 0; // 分数
    // 存放蛇的坐标
    int[] snakex = new int[750];
    int[] snakey = new int[750];
    String fx = "R";  // 方向， R L U D
    Boolean isStarted = false; // 游戏开始标识符
    Boolean isFailed = false; // 游戏结束标识符

    Timer timer = new Timer(100,this);  //每间隔100ms监听一次键盘事件
    int foodx,foody;    // 食物的位置
    Random rand = new Random(); // 随机生成食物位置
    Clip bgm;   //背景音乐

    public MPanel(){
        loadImages();
        initSnake();
        this.setFocusable(true);// 可以获取焦点
        this.addKeyListener(this);  // 添加键盘监听
        timer.start();  //开启时钟
        loadBGM(); // 加载BGM
    }

    // 加载游戏所需图片
    private void loadImages() {
        InputStream is;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("images/title.jpg");
            title = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/body.png");
            body = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/up.png");
            up = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/down.png");
            down = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/left.png");
            left = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/right.png");
            right = new ImageIcon(ImageIO.read(is));

            is = this.getClass().getClassLoader().getResourceAsStream("images/food.png");
            food = new ImageIcon(ImageIO.read(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.setBackground(Color.WHITE);

        /* 设置布局 */
        // 用画笔把标题图片画在本画布的（25，11）处
        title.paintIcon(this, g,25,11);
        // 画一个黑框（主体部分）
        g.fillRect(25,75,850,600);
        // 长度和分数
        g.setColor(Color.WHITE);
        g.drawString("Len " +len,750,35);
        g.drawString("Score " +score,750,50);

//        // 画静态蛇
//        right.paintIcon(this,g,100,100);
//        body.paintIcon(this,g,75,100);
//        body.paintIcon(this,g,50,100);


        /* 设置贪吃蛇的初始化位置 */
        if (fx == "R"){
            right.paintIcon(this,g,snakex[0],snakey[0]);
        }else if (fx == "L"){
            left.paintIcon(this,g,snakex[0],snakey[0]);
        }else if (fx == "U"){
            up.paintIcon(this,g,snakex[0],snakey[0]);
        }else if (fx == "D"){
            down.paintIcon(this,g,snakex[0],snakey[0]);
        }
        for (int i=0;i<len;i++){
            body.paintIcon(this,g,snakex[i],snakey[i]);
        }

        // 随机生成食物的位置
        food.paintIcon(this,g,foodx,foody);

        /* 游戏尚未开始，显示提示信息 */
        if (isStarted == false){
            /* 设置提示信息 */
            g.setColor(Color.WHITE);
            g.setFont(new Font("arial",Font.BOLD,40));
            g.drawString("Press Space to Start",280,300);
        }
        /* 游戏失败，显示提示信息 */
        if (isFailed){
            /* 设置提示信息 */
            g.setColor(Color.RED);
            g.setFont(new Font("arial",Font.BOLD,40));
            g.drawString("Failed:Press Space to Start",220,300);
        }



    }


    // 初始化蛇和食物位置
    public void initSnake(){
        len = 3;
        snakex[0] = 100;
        snakey[0] = 100;
        snakex[1] = 75;
        snakey[1] = 100;
        snakex[2] = 50;
        snakey[2] = 100;
        foodx = 25 + 25 * rand.nextInt(34);
        foody = 75 + 25 * rand.nextInt(24);
        fx = "R"; // 重置方向
        score = 0; // 重置分数
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        // 按下空格键
        if (keyCode == KeyEvent.VK_SPACE){
            // 开始 和 重新开始
            if (isFailed){
                isFailed = false;
                initSnake();
            }else {
                isStarted = !isStarted;
            }
            repaint();
            //游戏开始播放音乐，游戏暂停 停止音乐
            if (isStarted){
                playBGM();
            }else {
                stopBGM();
            }
        }else if (keyCode == KeyEvent.VK_LEFT){ // 上下左右键
            fx = "L";
        }else if (keyCode == KeyEvent.VK_RIGHT){
            fx = "R";
        }else if (keyCode == KeyEvent.VK_UP){
            fx = "U";
        }else if (keyCode == KeyEvent.VK_DOWN){
            fx = "D";
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // 贪吃蛇的动作方法
    @Override
    public void actionPerformed(ActionEvent e) {
        // 开始游戏时，蛇开始移动
        if (isStarted && !isFailed){
            // 贪吃蛇循环移动（蛇身）
            for (int i=len-1;i>0;i--){
                snakex[i] = snakex[i-1];
                snakey[i] = snakey[i-1];
            }
            // 键盘监听蛇头的方向
            if (fx == "R"){
                snakex[0] = snakex[0] + 25;
                if (snakex[0] > 850)snakex[0] = 25;
            }else if (fx == "L"){
                snakex[0] = snakex[0] - 25;
                if (snakex[0] < 25)snakex[0] = 850;
            }else if (fx == "U"){
                snakey[0] = snakey[0] - 25;
                if (snakey[0] < 75)snakey[0] = 650;
            }else if (fx == "D"){
                snakey[0] = snakey[0] + 25;
                if (snakey[0] > 650)snakey[0] = 75;
            }

            // 如果食物和蛇头的位置重叠，蛇的长度+1，分数+1，食物随机生成
            if (snakex[0] == foodx && snakey[0] == foody){
                len++;
                score = score + 10;
                foodx = 25 + 25 * rand.nextInt(34);
                foody = 75 + 25 * rand.nextInt(24);
            }

            // 如果蛇头和蛇身重叠，则游戏结束
            for (int j=1; j<len; j++){
                if (snakex[j] == snakex[0] && snakey[j] == snakey[0]){
                    isFailed = true;
                }
            }
            // 每隔 100ms 重新画一遍
            repaint();
        }
        timer.start();
    }


    // 加载背景音乐
    private void loadBGM() {
        try {
            bgm = AudioSystem.getClip();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sound/bgm.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            bgm.open(ais);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 播放BGM
    private void playBGM(){
        bgm.loop(Clip.LOOP_CONTINUOUSLY); //循环播放
    }

    // 停止播放BGM
    private void stopBGM(){
        bgm.stop();
    }

}
