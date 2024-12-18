import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import javax.swing.*;

public class loginPage extends JFrame {
    private FrameDragListener frameDragListener;
    private JTextField userID, password;
    private JLabel title, t_userID, t_password;
    private JButton loginButton, btn_Exit;

    public loginPage() {
        super("말할개");

        // 창 상단바 없애고 드래그 가능하도록 설정
        setUndecorated(true);
        frameDragListener = new FrameDragListener(this);

        // GUI 창의 크기와 위치, 배경색을 정하고 UI 패널 구성
        buildGUI();

        setVisible(true);
    }

    // GUI 창의 크기와 위치, 배경색을 정하고 UI 패널 구성
    private void buildGUI() {
        setSize(400, 600);
        setLocation(1000, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(255, 240, 180));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createLoginPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        mouseMove();
    }

    // 우측 상단 종료 버튼을 생성하는 함수
    private void createExitBtn(JPanel panel) {
        Image img = Toolkit.getDefaultToolkit().getImage("image/Exit_btn.png");
        img = img.getScaledInstance(25,25,Image.SCALE_DEFAULT);
        Icon icon = new ImageIcon(img);
        btn_Exit = new JButton(icon);
        btn_Exit.setBounds(375, 0, 25, 25);

        btn_Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 부모 패널(loginPage) 자체에 부착
        panel.add(btn_Exit);
    }

    // 창 상단을 드래그 가능하도록 하는 함수
    private void mouseMove() {
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null); // 절대 레이아웃으로 설정
        panel.setBackground(new Color(255, 240, 180));
        panel.setPreferredSize(new Dimension(getWidth(), 350)); // 패널 높이 조정

        // 종료 버튼 생성 및 추가
        createExitBtn(panel);

        // 제목 라벨 추가
        JLabel titleLabel = new JLabel("말 할 개", JLabel.CENTER);
        titleLabel.setFont(new Font("한컴 울주 반구대 암각화체", Font.PLAIN, 60));
        titleLabel.setBounds(100, 35, 200, 80); // 제목 위치 설정
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(200, 255, 200));
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(titleLabel);

        // 이미지 추가
        ImageIcon icon = new ImageIcon("image/dog5.png"); // 이미지 경로 설정
        Image scaledImage = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // 크기 조정
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(50, 75, 300, 300); // 이미지 위치 및 크기 설정
        panel.add(imageLabel);

        return panel;
    }


    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 240, 180));

        // ID 패널
        JPanel p_userID = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p_userID.setBackground(new Color(255, 240, 180));

        t_userID = new JLabel("ID ");
        t_userID.setFont(new Font("SF Pro", Font.BOLD, 14));
        userID = new JTextField(15);

        p_userID.add(t_userID);
        p_userID.add(userID);

        // PW 패널
        JPanel p_password = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p_password.setBackground(new Color(255, 240, 180));

        t_password = new JLabel("PW");
        t_password.setFont(new Font("SF Pro", Font.BOLD, 14));
        password = new JPasswordField(15);

        p_password.add(t_password);
        p_password.add(password);

        // 패널에 추가
        panel.add(Box.createVerticalStrut(50));
        panel.add(p_userID);
        panel.add(Box.createVerticalStrut(10)); // ID/PW 사이 간격
        panel.add(p_password);

        return panel;

    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(255, 240, 180));

        // 로그인 버튼
        loginButton = new JButton(" 로 그 인 ");
        loginButton.setFont(new Font("한컴 울주 반구대 암각화체", Font.PLAIN, 25));
        loginButton.setBackground(new Color(200, 255, 200));
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // 버튼 동작 추가
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredID = userID.getText().trim();
                String enteredPW = password.getText().trim();

                if (authenticate(enteredID, enteredPW)) {
                    try {
                        Socket socket = new Socket("localhost", 12345); // 새 소켓 생성
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        out.println("LOGIN:" + enteredID);

                        String serverResponse = in.readLine();
                        if ("WELCOME".equals(serverResponse)) {
                            JOptionPane.showMessageDialog(null, enteredID + "님 환영합니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                            dispose(); // 로그인 창 닫기
                            new profilePage(enteredID, socket); // 프로필 페이지 열기 (소켓 유지)
                        } else if (serverResponse.startsWith("ERROR")) {
                            JOptionPane.showMessageDialog(null, serverResponse, "로그인 실패", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "서버 연결 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "로그인 실패! ID 또는 PW를 확인하세요.", "실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        panel.add(Box.createVerticalStrut(30)); // 로그인 패널 - 버튼 사이 간격
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(60)); // 바닥 - 버튼 사이 간격

        return panel;
    }

    private boolean authenticate(String enteredID, String enteredPW) {
        // CSV 파일 경로 설정
        String csvFilePath = "accounts.csv"; // CSV 파일 경로

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) { // 더 읽을 내용이 없을 때까지 csv 파일을 한 줄씩 읽어옴
                String[] credentials = line.split(","); // ID와 PW는 콤마로 구분된다고 가정
                if (credentials.length == 2) {
                    String storedID = credentials[0].trim();
                    String storedPW = credentials[1].trim();

                    // csv 파일에서 읽어온 id와 pw가 입력한 값과 같으면 인증 성공, true를 리턴.
                    if (storedID.equals(enteredID) && storedPW.equals(enteredPW)) {
                        return true; // 인증 성공
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // 인증 실패
    }

    public static void main(String[] args) {
        new loginPage();
    }
}
