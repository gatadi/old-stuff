import java.util.Arrays ;
import java.util.Vector ;


class CSudoku
{
    private int N = 9 ;
    private String[] code = null ;
    private Vector result = null ;

    private String[] values = null ;


    public CSudoku(String[] sudoku)
    {
        code = sudoku ;
        values = new CPermitations("123456789").values() ;
        result = fillSudoku() ;
    }


    public Vector getResult()
    {
        return result ;
    }


    private Vector fillSudoku()
    {
        StringBuffer[] rules = null ;
        StringBuffer[] subRules = null ;
        String[] result = new String[N] ;
        Vector vPossibleMatches = new Vector(N) ;

        for(int i=0; i<N; i++)
        {
            rules = createRules(code, i) ;
            subRules = createSubRules(code) ;
            vPossibleMatches.add( find(i, code[i], rules, subRules) );
        }

        String[] finalCode = new String[N] ;
        System.arraycopy(code, 0, finalCode, 0, N );

        Vector[] arrayOfVectors = new Vector[N] ;
        for(int n=0; n<vPossibleMatches.size(); n++){
            arrayOfVectors[n] = (Vector)vPossibleMatches.elementAt(n) ;
            System.out.println("[" + arrayOfVectors[n].size() + "]");
        }

        Vector vResults = new Vector() ;

        int count = 0 ;
        for(int i0=0; i0<arrayOfVectors[0].size(); i0++)
        {
            finalCode[0] = arrayOfVectors[0].elementAt(i0).toString();

            rules = createRules(finalCode, 1) ;
            subRules = createSubRules(finalCode) ;
            arrayOfVectors[1] = find(1, finalCode[1], rules, subRules) ;



            for(int i1=0; i1<arrayOfVectors[1].size(); i1++)
            {
                finalCode[1] = arrayOfVectors[1].elementAt(i1).toString();

                rules = createRules(finalCode, 2) ;
                subRules = createSubRules(finalCode) ;
                arrayOfVectors[2] = find(2, finalCode[2], rules, subRules) ;

                for(int i2=0; i2<arrayOfVectors[2].size(); i2++)
                {
                    finalCode[2] = arrayOfVectors[2].elementAt(i2).toString();

                    rules = createRules(finalCode, 3) ;
                    subRules = createSubRules(finalCode) ;
                    arrayOfVectors[3] = find(3, finalCode[3], rules, subRules) ;

                    for(int i3=0; i3<arrayOfVectors[3].size(); i3++)
                    {
                        finalCode[3] = arrayOfVectors[3].elementAt(i3).toString();

                        rules = createRules(finalCode, 4) ;
                        subRules = createSubRules(finalCode) ;
                        arrayOfVectors[4] = find(4, finalCode[4], rules, subRules) ;

                        for(int i4=0; i4<arrayOfVectors[4].size(); i4++)
                        {
                            finalCode[4] = arrayOfVectors[4].elementAt(i4).toString();

                            rules = createRules(finalCode, 5) ;
                            subRules = createSubRules(finalCode) ;
                            arrayOfVectors[5] = find(5, finalCode[5], rules, subRules) ;

                            for(int i5=0; i5<arrayOfVectors[5].size(); i5++)
                            {
                                finalCode[5] = arrayOfVectors[5].elementAt(i5).toString();

                                rules = createRules(finalCode, 6) ;
                                subRules = createSubRules(finalCode) ;
                                arrayOfVectors[6] = find(6, finalCode[6], rules, subRules) ;
                                for(int i6=0; i6<arrayOfVectors[6].size(); i6++)
                                {
                                    finalCode[6] = arrayOfVectors[6].elementAt(i6).toString();

                                    rules = createRules(finalCode, 7) ;
                                    subRules = createSubRules(finalCode) ;
                                    arrayOfVectors[7] = find(7, finalCode[7], rules, subRules) ;
                                    for(int i7=0; i7<arrayOfVectors[7].size(); i7++)
                                    {
                                        finalCode[7] = arrayOfVectors[7].elementAt(i7).toString();

                                        rules = createRules(finalCode, 8) ;
                                        subRules = createSubRules(finalCode) ;
                                        arrayOfVectors[8] = find(8, finalCode[8], rules, subRules) ;

                                        for(int i8=0; i8<arrayOfVectors[8].size(); i8++)
                                        {
                                            finalCode[8] = arrayOfVectors[8].elementAt(0).toString() ;

                                            String[] tempFinalCode = new String[9] ;
                                            System.arraycopy(finalCode, 0, tempFinalCode, 0, 9);
                                            vResults.addElement(tempFinalCode) ;

                                            return vResults ;

                                        }
                                    }
                                    finalCode[7] = code[7] ;
                                }

                                finalCode[6] = code[6] ;
                            }

                            finalCode[5] = code[5] ;
                        }

                        finalCode[4] = code[4] ;
                    }

                    finalCode[3] = code[3] ;
                }
                finalCode[2] = code[2] ;
            }

            finalCode[1] = code[1] ;
        }


        return vResults ;
    }




    private StringBuffer[] createRules(String[] code, int rowIndex)
    {
        StringBuffer[] rules = new StringBuffer[code.length] ;
        for(int i=0; i<N; i++)
        {
            rules[i] = new StringBuffer(N) ;
            for(int j=0; j<code.length ; j++)
            {
                if( j == rowIndex ){
                    rules[i].append('0');
                }else{
                    rules[i].append(code[j].charAt(i)) ;
                }
            }
        }

        return rules ;
    }


    private StringBuffer[] createSubRules(String[] code)
    {
        StringBuffer[] rules = new StringBuffer[code.length] ;
        for(int i=0; i<N; i++){
            rules[i] = new StringBuffer(N) ;
        }

        int n = 0 ;
        for(int i=0; i<N; i++)
        {
            if( i>0 && i%3 == 0 ){
                n++ ;
            }else{
                n = 3*(i/3) ;
            }

            for(int j=0; j<N; j++)
            {
                if( j > 0 && j%3 == 0 ){
                    n++ ;
                }
                rules[n].append(code[i].charAt(j)) ;
            }
        }

        return rules ;
    }



    private Vector find(int rowIndex, String list, StringBuffer[] rules, StringBuffer[] subRules)
    {
        Vector vMatch = new Vector(10) ;
        for(int n=0; n<values.length; n++)
        {
            boolean match = true ;
            for(int i=0; i<N; i++)
            {
                if( list.charAt(i) != '0' )
                {
                    if( values[n].charAt(i) != list.charAt(i) ) {
                        match = false ;
                        break ;
                    }
                }
            }
            if(match)
            {
                for(int i=0; i<N; i++)
                {
                    for(int j=0; j<N; j++)
                    {
                        if( values[n].charAt(i) == rules[i].charAt(j) )
                        {
                            match = false ;
                            break ;
                        }
                    }
                }
            }

            if(match)
            {
                int x = 3*(rowIndex/3) ;
                for(int i=0; i<N; i++)
                {
                    if( i > 0 && i%3 == 0 ){
                        x++ ;
                    }
                    for(int j=0; j<N; j++)
                    {
                        int d = ( (i%3) + (rowIndex%3)*3 ) ;

                        if( values[n].charAt(i) == subRules[x].charAt(j) )
                        {
                            if( j != d )
                            {
                                match = false ;
                                break ;
                            }

                        }
                    }
                }

            }

            if( match ){
                vMatch.add(values[n]);
                //System.out.println(values[n]);
            }
        }

        return vMatch ;

    }   //end of find()




    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCPermitations.main()") ;

        String[] code = new String[] {
                            "810000703",
                            "000607008",
                            "902310600",
                            "040070560",
                            "007901200",
                            "063040090",
                            "004092106",
                            "600504000",
                            "708000059"
                          } ;

        code = new String[] {   //very easy
                            "260370004",
                            "000004506",
                            "035090100",
                            "050402003",
                            "602000708",
                            "900706020",
                            "007050640",
                            "304900000",
                            "500041087"
                          } ;

        code = new String[] {   // easy
                            "790000052",
                            "103602807",
                            "000000000",
                            "001906500",
                            "070504010",
                            "002301400",
                            "000000000",
                            "908103705",
                            "530000068"
                          } ;

        code = new String[] {            //hard Nov'14th 2005
                            "00050040",
                            "068300009",
                            "000008000",
                            "090800500",
                            "102000706",
                            "007006010",
                            "000200000",
                            "600007280",
                            "040005000"
                          } ;


        long t1 = System.currentTimeMillis();
        CSudoku sudoku = new CSudoku(code) ;

        Vector result = sudoku.getResult() ;
        long t2 = System.currentTimeMillis() ;

        System.out.println("\nPossible Answers : " + result.size()) ;
        for(int n=0; n<result.size(); n++)
        {
            System.out.println("\nPossibility: " + n) ;
            String[] answer = (String[])result.elementAt(n) ;
            for(int i=0; i<answer.length; i++){
                System.out.println(answer[i]);
            }
        }

        System.out.println("Total time to fill sudoku is : " + (t2-t1)/1000 + " Seconds");

    }

}   //end of CSudoku class
