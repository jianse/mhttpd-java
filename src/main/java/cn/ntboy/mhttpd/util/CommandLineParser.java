package cn.ntboy.mhttpd.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CommandLineParser{

    @Getter
    @Setter
    private String longOptionPrefix = "--";

    @Getter
    @Setter
    private String shortOptionPrefix = "-";

    @Getter
    @Setter
    private boolean parseLongOption = true;

    @Getter
    @Setter
    private boolean parseShortOption = true;

    private boolean caseSensitive = true;

    Map<String,Option> optionMap = new HashMap<>();
    private Handler defaultHandler = (op,p,arg) ->{
        System.out.println(op + " at "+p+" is not support!");
    };

    public void addOption(String longOption,String shortOption,ArgState argState,Handler handler){
        Option option = new Option(longOption,shortOption,argState,handler);
        optionMap.put(longOption,new Option(longOption,shortOption,argState,handler));
        optionMap.put(shortOption,new Option(longOption,shortOption,argState,handler));
    }

    public void addOption(Option option){
        optionMap.put(option.getLongOption(),option);
        optionMap.put(option.getShortOption(),option);
    }

    public void parse(String[] args) throws CommandLineParseException {
        for(int i=0;i<args.length;i++){
            String temp = args[i];
            if(temp.startsWith(longOptionPrefix)||temp.startsWith(shortOptionPrefix)){
                if(temp.startsWith(longOptionPrefix)){
                    temp = temp.substring(longOptionPrefix.length());
                }
                if(temp.startsWith(shortOptionPrefix)){
                    temp = temp.substring(shortOptionPrefix.length());
                }
                Option option = (Option) optionMap.get(temp);
                if(option==null){
                    this.defaultHandler.handle(temp,i,null);
                }else{
                    ArgState argState = option.getArgState();
                    if(argState.equals(ArgState.HAVE_ARG)){
                        if(args[i+1].startsWith(longOptionPrefix)||args[i+1].startsWith(shortOptionPrefix)){
                            //next is a option
                            throw new CommandLineParseException("unexpected option arg at " + i+ " after "+ "temp");
                        }else {
                            option.getHandler().handle(temp,i,args[i+1]);
                            i++;
                            continue;
                        }
                    }
                    if(argState.equals(ArgState.OPTIONAL_ARG)){
                        if(args[i+1].startsWith(longOptionPrefix)||args[i+1].startsWith(shortOptionPrefix)){
                            //next is a option
                            option.getHandler().handle(temp,i,null);
                            continue;
                        }else {
                            option.getHandler().handle(temp,i,args[i+1]);
                            i++;
                            continue;
                        }
                    }
                    if(argState.equals(ArgState.NO_ARG)){
                        option.getHandler().handle(temp,i,null);
                    }
                }


            }

        }
    }

    @FunctionalInterface
    public interface Handler{
        void handle(String option,int optionPosition,String arg);
    }

    public enum ArgState{
        HAVE_ARG,NO_ARG,OPTIONAL_ARG
    }


    public class Option{
        @Getter @Setter
        private String longOption;
        @Getter @Setter
        private String shortOption;
        @Getter @Setter
        private ArgState argState;
        @Getter @Setter
        private Handler handler;


        public Option(String longOption, String shortOption, ArgState argState, Handler handler) {
            this.longOption = longOption;
            this.shortOption = shortOption;
            this.argState = argState;
            this.handler = handler;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Option){
                Option option = (Option) obj;
                if((this.longOption.equals(option.longOption))||
                        (this.shortOption.equals(option.shortOption))){
                    return true;
                }
            }else if(obj instanceof String){
                String str=(String)obj;
                if(str.equals(this.longOption)||str.equals(this.shortOption)){
                    return true;
                }

            }
            return super.equals(obj);
        }
    }
}
