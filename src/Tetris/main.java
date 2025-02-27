package Tetris;

import javax.swing.JFrame;

public class main {

    public static void main(String[] args) {
        
        JFrame window = new JFrame("Tetris"); //Tạo một cửa sổ GUI với tiêu đề "Tetris"
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Khi nhấn dấu X trên cửa sổ, chương trình sẽ thoát.

        window.setResizable(false);//Ngăn người dùng thay đổi kích thước cửa sổ.

        GamePanel gp = new GamePanel(); // CHEN GAME PANEL VAO WINDOW
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null);//Đặt cửa sổ ở chính giữa màn hình.
        window.setVisible(true); // Hiển thị cửa sổ lên màn hình.

        gp.launchGame();
    }
}
