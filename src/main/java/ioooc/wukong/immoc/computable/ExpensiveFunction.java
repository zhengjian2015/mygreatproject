package ioooc.wukong.immoc.computable;

/**
 * 耗时计算的实现类，实现了Computable接口，但是本身不具备缓存能力，不需要考虑缓存的事情
 */
public class ExpensiveFunction implements Computable<String,Integer>{

    @Override
    public Integer computer(String arg) throws Exception {
        Thread.sleep(5000);
        System.out.println("-----------------");
        return new Integer(arg);
    }
}
