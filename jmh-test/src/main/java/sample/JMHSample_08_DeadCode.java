package sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * The downfall of many benchmarks is Dead-Code Elimination (DCE): compilers
 * are smart enough to deduce some computations are redundant and eliminate
 * them completely. If the eliminated part was our benchmarked code, we are
 * in trouble.
 * <p>
 * Fortunately, JMH provides the essential infrastructure to fight this
 * where appropriate: returning the result of the computation will ask JMH
 * to deal with the result to limit dead-code elimination (returned results
 * are implicitly consumed by Blackholes, see JMHSample_09_Blackholes).
 * <p>
 * 结果：由于measureWrong中的无用计算，编译器会消除，导致该速度与baseline几乎一样
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

    private double x = Math.PI;

    @Benchmark
    public void baseline() {
        // do nothing, this is JMHSample_08_DeadCode.txt baseline
    }

    @Benchmark
    public void measureWrong() {
        // This is wrong: result is not used and the entire computation is optimized away.
        Math.log(x);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the result is being used.
        return Math.log(x);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the unrealistically fast calculation in with measureWrong(),
     * while realistic measurement with measureRight().
     *
     * You can run this test:
     *
     * JMHSample_08_DeadCode.txt) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_08 -f 1
     *    (we requested single fork; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_08_DeadCode.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
