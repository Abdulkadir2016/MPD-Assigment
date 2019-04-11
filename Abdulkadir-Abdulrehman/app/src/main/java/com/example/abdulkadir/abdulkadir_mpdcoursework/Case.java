package com.example.abdulkadir.abdulkadir_mpdcoursework;


//StudentName:Abdulkadir Abdulrehman
//        StudentID           S1712579
//        MPD: Coursework
//        program of study:computing



public class Case {
    String camelCase(String str)
    {
        StringBuilder builder = new StringBuilder(str);

        boolean isLastSpace = true;

        for(int i = 0; i < builder.length(); i++)
        {
            char ch = builder.charAt(i);
            if(isLastSpace && ch >= 'a' && ch <='z')
            {
                builder.setCharAt(i, (char)(ch + ('A' - 'a') ));
                isLastSpace = false;
            }
            else if (ch != ' ')
                isLastSpace = false;
            else
                isLastSpace = true;
        }
        return builder.toString();
    }
}
