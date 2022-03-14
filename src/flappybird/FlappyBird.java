package flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    public static FlappyBird flappyBird;
    public final int WIDTH = 800, HEIGHT = 800;
    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> columns;
    public int ticks, yMotion, score, highscore;
    public boolean gameOver, started;
    public Random rand;

    private Image img;
    JFrame jframe;
    
    public FlappyBird()
    {
        jframe = new JFrame("Flappy Bird");
    	Timer timer = new Timer(20, this);
        
        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void addColumn(boolean start)
    {
    	int space = 300;
        int width = 100;
    	int height = 50 + rand.nextInt(300);
        
    	if (start)
    	{
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
	    }
        else
        {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }
    
    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void jump()
    {
        if (gameOver)
        {
            JOptionPane.showMessageDialog(jframe, "Điểm của bạn: " + score + "\n" + "Điểm cao: " + highscore + "\nBấm OK để bắt đầu trò chơi mới");
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }
        
        if (!started)
        {
            started = true;
        }
        else if (!gameOver)
        {
            if (yMotion > 0)
            {
                yMotion = 0;
            }
            yMotion -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int speed = 10;
        ticks++;
        if (started)
        {  
            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }
            
            if (ticks % 2 == 0 && yMotion < 15)
            {
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0)
                {
                    columns.remove(column);
                    if (column.y == 0)
                    {
                        addColumn(false);
                    }
                }
            }
                
            bird.y += yMotion;

            for (Rectangle column : columns)
            {
                if (column.y == 0 && bird.x + bird.width / 4 > column.x + column.width / 2 - 10 && bird.x + bird.width / 4 < column.x + column.width / 2 + 10)
                {
                    score++;
                }
                        
                if (column.intersects(bird))
                {
                    gameOver = true;
                    if (bird.x <= column.x)
                    {
                        bird.x = column.x - bird.width;
                    }
                    else
                    {
                        if (column.y != 0)
                        {
                            bird.y = column.y - bird.height;
                        }
                        else if (bird.y < column.height)
                        {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0)
            {
                gameOver = true;
            }
            if (bird.y + yMotion >= HEIGHT - 120)
            {
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }
        }

        renderer.repaint();
    }

    public void repaint(Graphics g)
    {
        g.setColor(Color.cyan);
	    g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);
            
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);
        
        try
        {
            img = ImageIO.read(new File("bird.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        g.setColor(Color.red);
        g.drawImage(img, bird.x, bird.y, bird.width, bird.height, null);

        for (Rectangle column : columns)
        {
            paintColumn(g, column);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 25));
            
        if (!started)
        {
            g.drawString("Bấm F để nhảy!", 320, HEIGHT / 2 - 50);
        }

        if (gameOver)
        {
            g.drawString("Trò chơi kết thúc! Click để tiếp tục.", 200, HEIGHT / 2);
        }
            
        if (!gameOver && started)
        {
            if(score > highscore)
            {
                highscore = score;
            }
            g.drawString(String.valueOf(score), WIDTH / 2, 100);
        }
    }

    public static void main(String[] args)
    {
        flappyBird = new FlappyBird();
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1)
        {
            jump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_F)
        {
            jump();
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }
}