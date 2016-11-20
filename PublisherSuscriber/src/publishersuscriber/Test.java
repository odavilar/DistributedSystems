/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author uidp1416
 */
public class Test {

    public static void main(String[] args) {
        String str = "#topic #topic2 #topic3 : Mensaje bien chido";

        Pattern p = Pattern.compile("(#.+)\\s:\\s(.*)");
        Matcher m = p.matcher(str);

        if (m.matches()) {
            String firstValue = m.group(1);
            System.out.println(firstValue);
            String secondValue = m.group(2);
            System.out.println(secondValue);
            String topics[] = firstValue.split(" ");
            for(int i = 0; i < topics.length; i++)
            {
                topics[i] = topics[i].substring(1);
                System.out.println(topics[i]);
            }
        }
        else
        {
            System.out.println("Does not match format");
        }
    }
}
