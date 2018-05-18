import javafx.scene.control.TextField;

public class Mainscreen {

    /*
    * TAB 1
    * */

    public TextField tab1Input1;
    public TextField tab1Input2;
    public TextField tab1Output1;
    public TextField tab1Output2;

    public void tab1Cal1(){
        String input = tab1Input1.getText();
        FloatingPoint16 num = FloatingPoint16.makeOne(input);
        if (num == null){
            tab1Output1.setText("INVALID INPUT!");
        }else{
            tab1Output1.setText("" + num.val());
        }
    }

    public void tab1Cal2(){
        String input = tab1Input2.getText();
        try {
            FloatingPoint16 num = new FloatingPoint16(Double.parseDouble(input));
            tab1Output2.setText(num.getBits().replaceAll("^([01]{"+FloatingPoint16.SIZE+"})([01]{"+FloatingPoint16.SIZE+"})$", "$1.$2"));
        }catch (Exception e){
            tab1Output2.setText("INVALID NUMBER!");
        }
    }

    public void tab1Clear1(){
        tab1Input1.setText("");
        tab1Output1.setText("");
    }

    public void tab2Clear2(){
        tab1Input2.setText("");
        tab1Output2.setText("");
    }

    /*
    * TAB 2
    * */

    public TextField tab2Input1;
    public TextField tab2Input2;
    public TextField tab2Output1Bin;
    public TextField tab2Output1Dec;
    public TextField tab2Output2Bin;
    public TextField tab2Output2Dec;

    public void tab2Mult(){
        FloatingPoint16 input1 = FloatingPoint16.makeOne(tab2Input1.getText());
        FloatingPoint16 input2 = FloatingPoint16.makeOne(tab2Input2.getText());

        if (input1 == null || input2 == null){
            tab2Output1Bin.setText("INVALID INPUT!");
            tab2Output1Dec.setText("INVALID INPUT!");
        }else{
            input1.mult(input2);
            int size = FloatingPoint16.SIZE;
            tab2Output1Bin.setText(input1.getBits().replaceAll("^([01]{"+size+"})([01]{"+size+"})$", "$1.$2"));
            tab2Output1Dec.setText("" + input1.val());
        }
    }
    public void tab2Div(){
        FloatingPoint16 input1 = FloatingPoint16.makeOne(tab2Input1.getText());
        FloatingPoint16 input2 = FloatingPoint16.makeOne(tab2Input2.getText());

        if (input1 == null || input2 == null || input2.compareZero() == 0){
            tab2Output2Bin.setText("INVALID INPUT!");
            tab2Output2Dec.setText("INVALID INPUT!");
        }else{
            input1.div(input2);
            int size = FloatingPoint16.SIZE;
            tab2Output2Bin.setText(input1.getBits().replaceAll("^([01]{"+size+"})([01]{"+size+"})$", "$1.$2"));
            tab2Output2Dec.setText("" + input1.val());
        }
    }
    public void tab2Clear(){
        tab2Input1.setText("");
        tab2Input2.setText("");
        tab2Output1Bin.setText("");
        tab2Output1Dec.setText("");
        tab2Output2Bin.setText("");
        tab2Output2Dec.setText("");
    }



    /*
    * TAB 3
    * */

    public TextField tab3Input;
    public TextField tab3Output1;
    public TextField tab3Output2;

    public void tab3Clear(){
        tab3Input.setText("");
        tab3Output1.setText("");
        tab3Output2.setText("");
    }

    public void tab3Cal(){
        String input = tab3Input.getText();
        FloatingPoint16 num = FloatingPoint16.makeOne(input);
        if (num == null){
            tab3Output1.setText("INVALID INPUT");
            tab3Output1.setText("INVALID INPUT");
        }else{
            int size = FloatingPoint16.SIZE;
            tab3Output1.setText(num.getOnesCompletion().getBits().replaceAll("^([01]{"+size+"})([01]{"+size+"})$", "$1.$2"));
            tab3Output2.setText(num.getTwosCompletion().getBits().replaceAll("^([01]{"+size+"})([01]{"+size+"})$", "$1.$2"));
        }
    }
}