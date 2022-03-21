package flappyBird;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.imageio.ImageIO;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    public static FlappyBird flappyBird;
    private final int WIDTH = 800, HEIGHT = 800;
    private JFrame frame;
    private Renderer render;
    private ArrayList<Rectangle> columns;
    private Rectangle bird;
    private Image img;
    private int ticks, motion, score, highScore;
    private boolean started, gameOver;
    private Random rnd;

    public FlappyBird()
    {
        frame = new JFrame("Flappy Bird");
    	Timer timer = new Timer(20, this);
        
        render = new Renderer();
        rnd = new Random();

        frame.add(render);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
        columns = new ArrayList<Rectangle>();

        try
        {
            File f = new File("highest_score.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null)
            {
                highScore = Integer.parseInt(line);
            }
            fr.close();
            br.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
        System.out.println("Start!");
    }

    public void addColumn(boolean start)
    {
    	int space = 300;
        int width = 100;
    	int height = 50 + rnd.nextInt(300);
        
    	if(start)
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
        if(gameOver)
        {
            System.out.println("End!");
            JOptionPane.showMessageDialog(frame,"Score: " + score + "\n" + "High score: " + highScore + "\nClick OK to start new game");
            try
            {
                File f = new File("highest_score.txt");
                FileWriter fw = new FileWriter(f);
                String s = String.valueOf(highScore);
                fw.write(s);
                fw.close();
            }
            catch (IOException e)
            {
                System.out.println("Error: " + e);
            }
            finally
            {
                System.out.println("Score: " + score + "\n");
            }
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
            columns.clear();
            motion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }
        
        if(!started)
        {
            started = true;
        }
        else if(!gameOver)
        {
            if(motion > 0)
            {
                motion = 0;
            }
            motion -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int speed = 10;
        ticks++;
        if(started)
        {
            for(int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }
            
            if(ticks % 2 == 0 && motion < 15)
            {
                motion += 2;
            }

            for(int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                if(column.x + column.width < 0)
                {
                    columns.remove(column);
                    if(column.y == 0)
                    {
                        addColumn(false);
                    }
                }
            }
                
            bird.y += motion;

            for(Rectangle column: columns)
            {
                if(column.y == 0 && bird.x + bird.width / 4 > column.x + column.width / 2 - 10 && bird.x + bird.width / 4 < column.x + column.width / 2 + 10)
                {
                    score++;
                }
                        
                if(column.intersects(bird))
                {
                    gameOver = true;
                    if(bird.x <= column.x)
                    {
                        bird.x = column.x - bird.width;
                    }
                    else
                    {
                        if(column.y != 0)
                        {
                            bird.y = column.y - bird.height;
                        }
                        else if(bird.y < column.height)
                        {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if(bird.y > HEIGHT - 120 || bird.y < 0)
            {
                gameOver = true;
            }
            if(bird.y + motion >= HEIGHT - 120)
            {
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }
        }

        render.repaint();
    }

    public void scene(Graphics g)
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
        g.setFont(new Font("Arial", 1, 35));
            
        if(!started)
        {
            g.drawString("Click to continue!", WIDTH / 2 - 125, HEIGHT / 2 - 50);
        }

        if(gameOver)
        {
            g.drawString("You die!", WIDTH / 2 - 70, HEIGHT / 2 - 100);
            started = false;
        }
        
        if(!gameOver && started)
        {
            if(score > highScore)
            {
                highScore = score;
            }
            g.drawString(String.valueOf(score), WIDTH / 2, 100);
        }
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
        if(e.getKeyCode() == KeyEvent.VK_F)
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

    public static void main(String[] args) throws Exception
    {
        flappyBird = new FlappyBird();
    }
}