package flappybird;

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
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.imageio.ImageIO;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    public static FlappyBird flappyBird;
    private final int CHIEURONG = 800, CHIEUCAO = 800;
    private JFrame giaoDien;
    private DoHoa doHoa;
    private ArrayList<Rectangle> nCot;
    private Rectangle chim;
    private Image nhanVat;
    private int doNay, lucKeo, diem, diemCao;
    private boolean batDau, ketThuc;
    private Random ngauNhien;

    public FlappyBird()
    {
        giaoDien = new JFrame("Flappy Bird");
    	Timer thoiGian = new Timer(20, this);
        
        doHoa = new DoHoa();
        ngauNhien = new Random();

        giaoDien.add(doHoa);
        giaoDien.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        giaoDien.setSize(CHIEURONG, CHIEUCAO);
        giaoDien.addMouseListener(this);
        giaoDien.addKeyListener(this);
        giaoDien.setResizable(false);
        giaoDien.setVisible(true);

        chim = new Rectangle(CHIEURONG / 2 - 10, CHIEUCAO / 2 - 10, 40, 40);
        nCot = new ArrayList<Rectangle>();

        themCot(true);
        themCot(true);
        themCot(true);
        themCot(true);

        thoiGian.start();
    }

    public void themCot(boolean start)
    {
    	int khoangCach = 300;
        int chieuRong = 100;
    	int chieuCao = 50 + ngauNhien.nextInt(300);
        
    	if(start)
    	{
            nCot.add(new Rectangle(CHIEURONG + chieuRong + nCot.size() * 300, CHIEUCAO - chieuCao - 120, chieuRong, chieuCao));
            nCot.add(new Rectangle(CHIEURONG + chieuRong + (nCot.size() - 1) * 300, 0, chieuRong, CHIEUCAO - chieuCao - khoangCach));
	    }
        else
        {
            nCot.add(new Rectangle(nCot.get(nCot.size() - 1).x + 600, CHIEUCAO - chieuCao - 120, chieuRong, chieuCao));
            nCot.add(new Rectangle(nCot.get(nCot.size() - 1).x, 0, chieuRong, CHIEUCAO - chieuCao - khoangCach));
        }
    }
    
    public void veCot(Graphics g, Rectangle cot)
    {
        g.setColor(Color.green.darker());
        g.fillRect(cot.x, cot.y, cot.width, cot.height);
    }

    public void nhay()
    {
        if(ketThuc)
        {
            JOptionPane.showMessageDialog(giaoDien, "Điểm của bạn: " + diem + "\n" + "Điểm cao: " + diemCao + "\nBấm OK để bắt đầu trò chơi mới");
            chim = new Rectangle(CHIEURONG / 2 - 10, CHIEUCAO / 2 - 10, 40, 40);
            nCot.clear();
            lucKeo = 0;
            diem = 0;

            themCot(true);
            themCot(true);
            themCot(true);
            themCot(true);

            ketThuc = false;
        }
        
        if(!batDau)
        {
            batDau = true;
        }
        else if(!ketThuc)
        {
            if(lucKeo > 0)
            {
                lucKeo = 0;
            }
            lucKeo -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int vanToc = 10;
        doNay++;
        if(batDau)
        {  
            for(int i = 0; i < nCot.size(); i++)
            {
                Rectangle cot = nCot.get(i);
                cot.x -= vanToc;
            }
            
            if(doNay % 2 == 0 && lucKeo < 15)
            {
                lucKeo += 2;
            }

            for(int i = 0; i < nCot.size(); i++)
            {
                Rectangle cot = nCot.get(i);
                if(cot.x + cot.width < 0)
                {
                    nCot.remove(cot);
                    if(cot.y == 0)
                    {
                        themCot(false);
                    }
                }
            }
                
            chim.y += lucKeo;

            for(Rectangle cot : nCot)
            {
                if(cot.y == 0 && chim.x + chim.width / 4 > cot.x + cot.width / 2 - 10 && chim.x + chim.width / 4 < cot.x + cot.width / 2 + 10)
                {
                    diem++;
                }
                        
                if(cot.intersects(chim))
                {
                    ketThuc = true;
                    if(chim.x <= cot.x)
                    {
                        chim.x = cot.x - chim.width;
                    }
                    else
                    {
                        if(cot.y != 0)
                        {
                            chim.y = cot.y - chim.height;
                        }
                        else if(chim.y < cot.height)
                        {
                            chim.y = cot.height;
                        }
                    }
                }
            }

            if(chim.y > CHIEUCAO - 120 || chim.y < 0)
            {
                ketThuc = true;
            }
            if(chim.y + lucKeo >= CHIEUCAO - 120)
            {
                chim.y = CHIEUCAO - 120 - chim.height;
                ketThuc = true;
            }
        }

        doHoa.repaint();
    }

    public void taoHinh(Graphics g)
    {
        g.setColor(Color.cyan);
	    g.fillRect(0, 0, CHIEURONG, CHIEUCAO);
        
        g.setColor(Color.orange);
        g.fillRect(0, CHIEUCAO - 120, CHIEURONG, 120);
            
        g.setColor(Color.green);
        g.fillRect(0, CHIEUCAO - 120, CHIEURONG, 20);
        
        try
        {
            nhanVat = ImageIO.read(new File("bird.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        g.setColor(Color.red);
        g.drawImage(nhanVat, chim.x, chim.y, chim.width, chim.height, null);

        for (Rectangle cot : nCot)
        {
            veCot(g, cot);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 35));
            
        if(!batDau)
        {
            g.drawString("Click để tiếp tục!", CHIEURONG / 2 - 130, CHIEUCAO / 2 - 50);
        }

        if(ketThuc)
        {
            batDau = false;
        }
            
        if(!ketThuc && batDau)
        {
            if(diem > diemCao)
            {
                diemCao = diem;
            }
            g.drawString(String.valueOf(diem), CHIEURONG / 2, 100);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1)
        {
            nhay();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_F)
        {
            nhay();
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

    public static void main(String[] args)
    {
        flappyBird = new FlappyBird();
    }
}