import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloatingPoint16 {
    /*
     * PROPERTIES
     * */
    public static final int SIZE = 16;
    private static final int[] POWS = new int[SIZE + 1];
    private String bits;

    /*
     * CONSTRUCTORS
     * */

    public FloatingPoint16(double num) {
        int sign = num < 0 ? -1 : 1;
        this.bits = "";
        num = Math.abs(num);
        for (int i = SIZE - 1; i >= -SIZE; i--) {
            double cur = i >= 0 ? POWS[i] : 1.0 / POWS[Math.abs(i)];
            if (num >= cur) {
                num -= cur;
                this.bits += '1';
            } else {
                this.bits += '0';
            }
        }
        if (sign < 0) this.bits = getTwosCompletion().getBits();
    }

    private FloatingPoint16(String bits) {
        this.bits = bits;
    }

    static {
        POWS[0] = 1;
        for (int i = 1; i < SIZE + 1; i++)
            POWS[i] = POWS[i - 1] * 2;
    }

    /*
     * STATIC METHODS
     * */

    public static FloatingPoint16 makeOne(String bits) {
        Pattern p = Pattern.compile("^[01]{1," + SIZE + "}(\\.[01]{1," + SIZE + "}){0,1}$");
        Matcher m = p.matcher(bits);
        if (m.matches()) {
            if (bits.indexOf('.') < 0) {
                bits += zeros(SIZE);
            } else {
                bits += zeros(SIZE - (bits.length() - bits.indexOf('.') - 1));
            }
            bits = bits.replaceAll("\\.", "");
            bits = zeros(SIZE * 2 - bits.length()) + bits;
            return new FloatingPoint16(bits);
        } else {
            return null;
        }
    }

    private static String zeros(int num) {
        String ans = "";
        while (num-- > 0) ans += '0';
        return ans;
    }

    private static String ones(int num) {
        String ans = "";
        while (num-- > 0) ans += '1';
        return ans;
    }

    /*
     * GETTERS & SETTERS
     * */

    public String getBits() {
        return bits;
    }

    public double val() {
        double res = 0.0;
        String bs = bits;
        int sign = 1;
        if (bits.charAt(0) == '1') {
            sign = -1;
            bs = getTwosCompletion().bits;
        } else {
        }
        for (int i = 0; i < SIZE * 2; i++) {
            int p = SIZE - i - 1;
            double cur = p > 0 ? POWS[p] : 1.0 / POWS[Math.abs(p)];
            res += cur * (bs.charAt(i) - '0');
        }
        return res * sign;
    }

    public FloatingPoint16 getOnesCompletion() {
        String ans = "";
        for (int i = 0; i < SIZE * 2; i++)
            ans += (bits.charAt(i) - '0' + 1) % 2 + (i == SIZE - 1 ? "." : "");
        return makeOne(ans);
    }

    public FloatingPoint16 getTwosCompletion() {
        FloatingPoint16 oc = getOnesCompletion();
        oc.add(new FloatingPoint16(1.0 / POWS[SIZE]));
        return oc;
    }

    /*
     * OPERATIONS
     * */
    public int compareZero(){
        if (bits.indexOf('1') < 0) return 0;
        if (bits.charAt(0) == '1') return -1; else return 1;
    }

    public void add(FloatingPoint16 f) {
        int remainder = 0;
        String res = "";
        String fBits = f.getBits();

        for (int i = SIZE * 2 - 1; i >= 0; i--) {
            int tempSum = (bits.charAt(i) - '0') + (fBits.charAt(i) - '0') + remainder;
            // 0 1 2 3
            remainder = Math.floorDiv(tempSum, 2);
            res = (tempSum % 2) + res;
        }
        this.bits = res;
    }

    public void sub(FloatingPoint16 f) {
        add(f.getTwosCompletion());
    }


    public void mult(FloatingPoint16 f) {
        FloatingPoint16 res = new FloatingPoint16(0);
        int sign = 1;
        String lbits = bits;
        String fBits = f.getBits();

        if (lbits.charAt(0) == '1'){
            sign *= -1;
            lbits = getTwosCompletion().getBits();
        }
        if (fBits.charAt(0) == '1'){
            sign *= -1;
            fBits = f.getTwosCompletion().getBits();
        }

        for (int i = 0; i < SIZE * 2; i++)

            if (fBits.charAt(SIZE * 2 - i - 1) == '1') {
                String temp = lbits.substring(i - SIZE) + zeros(i - SIZE);
                temp = temp.replaceAll("^([01]{"+SIZE+"})([01]{"+SIZE+"})$", "$1.$2");
                res.add(makeOne(temp));
            }

        this.bits = sign > -1?res.bits:res.getTwosCompletion().bits;
    }



    public void div(FloatingPoint16 f) {
        if (f.compareZero() == 0){
            return;
        }
        if (f == this) {
            this.bits = new FloatingPoint16(1).getBits();
            return;
        }

        int sign = 1;
        String lbits = bits;
        String fBits = f.getBits();

        if (lbits.charAt(0) == '1'){
            sign *= -1;
            lbits = getTwosCompletion().getBits();
        }
        if (fBits.charAt(0) == '1'){
            sign *= -1;
            fBits = f.getTwosCompletion().getBits();
        }
        f = makeOne(fBits.replaceAll("^([01]{"+SIZE+"})([01]{"+SIZE+"})$", "$1.$2"));


        String cur = zeros(SIZE * 2);
        String res = "";
        for (int i = 0; i < SIZE * 2; i ++){
            cur = cur.substring(1) + lbits.charAt(i);

            FloatingPoint16 temp = makeOne(cur.replaceAll("^([01]{"+SIZE+"})([01]{"+SIZE+"})$", "$2") + "." + zeros(SIZE));
            temp.sub(f);
            if (temp.compareZero() >= 0){
                res += "1";
                cur = zeros(SIZE) + temp.getBits().substring(0, SIZE);
            }else{
                res += "0";
            }


        }

        this.bits = res;

    }

    /*
     * DEBUGGING
     * */

    private static final boolean DEBUG = true;

    public static void main(String[] args) {
        if (!DEBUG) return;

        FloatingPoint16 f = FloatingPoint16.makeOne("110.1");
        FloatingPoint16 i = FloatingPoint16.makeOne("110");
        FloatingPoint16 c = new FloatingPoint16(4);
        FloatingPoint16 d = new FloatingPoint16(4.5);
        FloatingPoint16 n = new FloatingPoint16(-4.5);
        FloatingPoint16 nc = c.getTwosCompletion();
        System.out.println(f.getBits() + " -> " + f.val());
        System.out.println(c.getBits() + " -> " + c.val());
        System.out.println(d.getBits() + " -> " + d.val());
        System.out.println(n.getBits() + " -> " + n.val());
        System.out.println(i.getBits() + " -> " + i.val());


        System.out.println("CỘNG");
        // ++
        c.add(d);
        System.out.println(c.getBits() + " -> " + c.val()); // 8.5 -> DUNG
        // --
        nc.add(n);
        System.out.println(nc.getBits() + " -> " + nc.val()); // -8.5 -> DUNG
        // +-
        c.add(n);
        System.out.println(c.getBits() + " -> " + c.val()); // 4.0 -> DUNG
        // -+
        n.add(d);
        System.out.println(n.getBits() + " -> " + n.val()); // 0.0 -> DUNG

        System.out.println("TRỪ");
        // -+
        n.sub(d);
        System.out.println(n.getBits() + " -> " + n.val()); // -4.5 -> DUNG
        // +-
        c.sub(n);
        System.out.println(c.getBits() + " -> " + c.val()); // 8.5 -> DUNG
        // --
        nc.sub(n);
        System.out.println(nc.getBits() + " -> " + nc.val()); // -4.0 -> DUNG
        // ++
        c.sub(d);
        System.out.println(c.getBits() + " -> " + c.val()); // 4.0 -> DUNG


        System.out.println("NHÂN");

        FloatingPoint16 m1 = new FloatingPoint16(2);
        FloatingPoint16 m2 = new FloatingPoint16(4);
        FloatingPoint16 m3 = new FloatingPoint16(-12);
        FloatingPoint16 m4 = new FloatingPoint16(-10);

        System.out.println(m1.getBits() + " -> " + m1.val()); // 2.0 -> DUNG
        System.out.println(m2.getBits() + " -> " + m2.val()); // 4.0 -> DUNG
        System.out.println(m3.getBits() + " -> " + m3.val()); // -12.0 -> DUNG
        System.out.println(m4.getBits() + " -> " + m4.val()); // -10.0 -> DUNG
        System.out.println("----");
        // ++
        m1.mult(m2); // 8.0 -> DUNG
        System.out.println(m1.getBits() + " -> " + m1.val());
        // --
        m3.mult(m4); // 120.0 -> DUNG
        System.out.println(m3.getBits() + " -> " + m3.val());
        // +-
        m1.mult(m4); // -80.0 -> DUNG
        System.out.println(m1.getBits() + " -> " + m1.val());
        // -+
        m4.mult(m2); // -40.0 -> DUNG
        System.out.println(m4.getBits() + " -> " + m4.val());

        // bonus

        FloatingPoint16 ml1 = new FloatingPoint16(1.5);
        FloatingPoint16 ml2 = new FloatingPoint16(-8.0);


        System.out.println("BONUS");
        System.out.println(ml1.getBits() + " -> " + ml1.val());
        System.out.println("x");
        System.out.println(ml2.getBits() + " -> " + ml2.val());
        ml1.mult(ml2);
        System.out.println("-----------------------");
        System.out.println(ml1.getBits() + " -> " + ml1.val());


        System.out.println("CHIA");
        ml1.div(ml2);
        System.out.println(ml1.getBits() + " -> " + ml1.val());
        System.out.println(new FloatingPoint16(1).getBits());
    }
}
