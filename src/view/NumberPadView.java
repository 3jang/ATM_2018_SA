package view;

import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NumberPadView extends JPanel
{
    private JPanel panel1;
    private JButton a1Button;   //숫자 키패드들
    private JButton a2Button;
    private JButton a3Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a7Button;
    private JButton a8Button;
    private JButton a9Button;
    private JButton deleteButton;
    private JButton okButton;
    private JButton a0Button;
    private JButton manButton;
    private JButton resetButton;
    private JPanel numPanel;
    private JTextField textField;           //다른 패널에 있는 textField(금액, 카드번호 등)
    private JPasswordField passwordField;   //비밀번호 입력창.
    IView relatedPanel; //연관된 패널.
    private int pointer = 0;

    public NumberPadView()
    {
        init();
    }

    private void init()
    {
        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(relatedPanel.getCurrentMode() == Mode.PASSWORD)  onButton(e.getSource(), relatedPanel.getJLabelArr());
                else onButton(e.getSource(), textField);
            }
        };

        //add button listener
        a1Button.addActionListener(listener);
        a2Button.addActionListener(listener);
        a3Button.addActionListener(listener);
        a4Button.addActionListener(listener);
        a5Button.addActionListener(listener);
        a6Button.addActionListener(listener);
        a7Button.addActionListener(listener);
        a8Button.addActionListener(listener);
        a9Button.addActionListener(listener);
        a0Button.addActionListener(listener);
        resetButton.addActionListener(listener);
        deleteButton.addActionListener(listener);
        okButton.addActionListener(listener);
        manButton.addActionListener(listener);

    }


    //number pad 와 연관된 panel 설정
    public void setRelatedPanel(IView pan)
    {
        relatedPanel = pan;
        textField = pan.getTextField();
    }


    //만원버튼 보이기 안보이기
    public void showManButton(boolean b)
    {
        manButton.setEnabled(b);
        manButton.setVisible(b);
    }



    //버튼 눌렀을때 행동 설정
    public void onButton(Object o, JTextField textField)
    {
        //어느 버튼을 눌렀는지 저장
        Object[] buttons = new Object[]
                {a0Button, a1Button, a2Button, a3Button, a4Button, a5Button, a6Button,
                        a7Button, a8Button, a9Button, deleteButton, okButton, resetButton, manButton};

        int but = 0;
        for(Object b: buttons)
        {
            if(o.equals(b)) break;
            but++;
        }


        //누른 버튼에 맞게 textField 내용을 갱신
        String temp = textField.getText();
        switch(but)
        {
            case 10:    //delete button
                onDeleteButton(temp);
                break;
            case 11:    //OK button
                onOKButton(temp);
                return;
            case 12:    //reset button
                onResetButton(temp);
                break;
            case 13:    //man button
                onManButton(temp);
                break;
            default:    //number buttons
                onNumButton(temp, but);
                break;
        }
    }


    //ok버튼 눌렀을 때
    private void onOKButton(String temp)
    {
        //TODO: 모델과 연동되는 코드 여기 작성.. temp는 현재 유저가 입력한 카드번호. 비밀번호.
        //에러 체크하기

        //입력 제대로 안 되었으면 에러
        if(temp.length() == 0 ||
                (pointer <4 && relatedPanel.getCurrentMode()==Mode.PASSWORD))
            ResultAlert.alert(this, "ERROR_EMPTY");

        //그 외 상황별 에러 체크
        else if(doModelMethod(relatedPanel.getCurrentMode())) return;

        //모든 트랜젝션 끝이면 알람 띄우기
        else if(relatedPanel.getNextMode()==Mode.ALERT)
            ResultAlert.alert(this, MainFrame.getInstance().getMenu());

        //아무것도 아니면 다음 화면으로 넘어가기
        else MainFrame.getInstance().changeView(relatedPanel.getNextMode());
    }


    private boolean doModelMethod(Mode currentMode)
    {   //TODO: 모델 관련 메소드.. 에러 체크 등등
        switch(currentMode)
        {
            case CARD : //카드 번호 입력할 때 체크해야 하는 메소드
                        //ResultAlert.alert(this, "ERROR_CARD");
                        //return true;
            case MONEY: //금액 입력할 때 체크해야 하는 메소드
                        //ResultAlert.alert(this, "ERROR_MONEY");
            case TRANSFER: //송금받을 카드 번호 입력할 때 체크해야 하는 메소드
                            //ResultAlert.alert(this,"ERROR_CARD");
                            //return true;
            case PASSWORD:  //비밀번호 입력할 때 체크해야 하는 메소드
                            //ResultAlert.alert(this, "ERROR_PW");
                            //ResultAlert.alert(this,"ERROR_BALANCE");
            default: break;
        }
        return false;
    }

    //만원 버튼 눌렀을 때 행동
    private void onManButton(String temp)
    {
        if(temp.length() == 0) return;
        if(temp.length()>3)
        {
            String str = temp.substring(temp.length()-4,temp.length());
            if(str.equals("0000")) return;
        }
        temp+= "0000";
        textField.setText(temp);
    }

    //resetButton 눌렀을때 행동
    private void onResetButton(String temp)
    {
        temp = "";
        textField.setText(temp);
    }

    //delete button 눌렀을 때 행동
    private void onDeleteButton(String temp)
    {
        if(temp.length() == 0) return;
        temp =  temp.substring(0, temp.length()-1);
        textField.setText(temp);
    }

    //그냥 숫자버튼 눌렀을 때
    private void onNumButton(String temp, int but)
    {
        temp+= Integer.toString(but);
        textField.setText(temp);
    }




    /** password 패널 이용할때 메소드 오버로딩 **/

    //password 와 연관되어있을때 버튼 설정
    public void onButton(Object o, JLabel[] larr)
    {
        //어느 버튼을 눌렀는지 저장
        Object[] buttons = new Object[]
                {a0Button, a1Button, a2Button, a3Button, a4Button, a5Button, a6Button,
                        a7Button, a8Button, a9Button, deleteButton, okButton, resetButton, manButton};

        int but = 0;
        for(Object b: buttons)
        {
            if(o.equals(b)) break;
            but++;
        }


        //누른 버튼에 맞게 textField 내용을 갱신
        StringBuilder temp = new StringBuilder();
        switch(but)
        {
            case 10:    //delete button
                pointer = onDeleteButton(larr, pointer);
                break;
            case 11:    //OK button
                for(JLabel label: larr)
                {
                    temp.append(label.getText());
                }
                onOKButton(temp.toString());
                return;
            case 12:    //reset button
                pointer = onResetButton(larr);
                break;
            case 13:    //man button ... not use
                onManButton(temp.toString());
                break;
            default:    //number buttons
                pointer = onNumButton(larr,pointer,but);
                break;
        }
    }

    //password화면일때 reset
    private int onResetButton(JLabel[] larr)
    {
        for(JLabel label : larr)
            label.setText(" ");
        return 0;
    }

    //password에서 숫자버튼 눌렀을때
    private int onNumButton(JLabel[] larr, int pointer, int but)
    {
        if(pointer > 3) return pointer;
        System.out.println(pointer);
        larr[pointer].setText("*");
        return pointer+1;
    }


    //password에서 delete 버튼 눌렀을때 돌리기
    private int onDeleteButton(JLabel[] larr, int pointer)
    {
        if(pointer == 0) return pointer;
        larr[--pointer].setText(" ");
        return pointer;
    }


    public JButton getManButton()
    {
        return manButton;
    }

    public JTextField getTextField()
    {
        return textField;
    }

    public static void setTextField(JTextField textField)
    {
        textField = textField;
    }

    public void initPointer()
    {
        pointer = 0;
    }
}