// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2004-9-10 13:15:36
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) ansi
// Source File Name:   FileRegistrySample.java

package org.frameworkset.platform.security.authentication;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author biaoping.yin
 * @version 1.0
 * 2005-2-6
 */
class RegExpSample implements Serializable
{

    private boolean match(String s, int i, int j, int k)
    {
        for(; k < expr.length; k++)
label0:
            {
                Object obj = expr[k];
                if(obj == STAR)
                {
                    if(++k >= expr.length)
                        return true;
                    if(expr[k] instanceof String)
                    {
                        String s1 = (String)expr[k++];
                        int l = s1.length();
                        for(; (i = s.indexOf(s1, i)) >= 0; i++)
                            if(match(s, i + l, j, k))
                                return true;

                        return false;
                    }
                    for(; i < j; i++)
                        if(match(s, i, j, k))
                            return true;

                    return false;
                }
                if(obj == ANY)
                {
                    if(++i > j)
                        return false;
                    break label0;
                }
                if(obj instanceof char[][])
                {
                    if(i >= j)
                        return false;
                    char c = s.charAt(i++);
                    char ac[][] = (char[][])obj;
                    if(ac[0] == NOT)
                    {
                        for(int j1 = 1; j1 < ac.length; j1++)
                            if(ac[j1][0] <= c && c <= ac[j1][1])
                                return false;

                        break label0;
                    }
                    for(int k1 = 0; k1 < ac.length; k1++)
                        if(ac[k1][0] <= c && c <= ac[k1][1])
                            break label0;

                    return false;
                }
                if(obj instanceof String)
                {
                    String s2 = (String)obj;
                    int i1 = s2.length();
                    if(!s.regionMatches(i, s2, 0, i1))
                        return false;
                    i += i1;
                }
            }

        return i == j;
    }

    public boolean match(String s)
    {
        return match(s, 0, s.length(), 0);
    }

    public boolean match(String s, int i, int j)
    {
        return match(s, i, j, 0);
    }

    public RegExpSample(String s)
    {
        Vector vector = new Vector();
        int i = s.length();
        StringBuffer stringbuffer = null;
        Object obj = null;
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            switch(c)
            {
            case 63: // '?'
                obj = ANY;
                break;

            case 42: // '*'
                obj = STAR;
                break;

            case 91: // '['
                int k = ++j;
                Vector vector1 = new Vector();
                for(; j < i; j++)
                {
                    c = s.charAt(j);
                    if(j == k && c == '^')
                    {
                        vector1.addElement(NOT);
                        continue;
                    }
                    if(c == '\\')
                    {
                        if(j + 1 < i)
                            c = s.charAt(++j);
                    } else
                    if(c == ']')
                        break;
                    char c1 = c;
                    if(j + 2 < i && s.charAt(j + 1) == '-')
                        c1 = s.charAt(j += 2);
                    char ac1[] = {
                        c, c1
                    };
                    vector1.addElement(ac1);
                }

                char ac[][] = new char[vector1.size()][];
                vector1.copyInto(ac);
                obj = ac;
                break;

            case 92: // '\\'
                if(j + 1 < i)
                    c = s.charAt(++j);
                break;
            }
            if(obj != null)
            {
                if(stringbuffer != null)
                {
                    vector.addElement(stringbuffer.toString());
                    stringbuffer = null;
                }
                vector.addElement(obj);
                obj = null;
            } else
            {
                if(stringbuffer == null)
                    stringbuffer = new StringBuffer();
                stringbuffer.append(c);
            }
        }

        if(stringbuffer != null)
            vector.addElement(stringbuffer.toString());
        expr = new Object[vector.size()];
        vector.copyInto(expr);
    }

    static final char NOT[] = new char[2];
    static final Integer ANY = new Integer(0);
    static final Integer STAR = new Integer(1);
    Object expr[];

}
