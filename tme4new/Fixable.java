/***********************************************************************
 * Adapated for COMP308 Java for Programmer,
 *		SCIS, Athabasca University
 *
 * Assignment: TME3, JLeger
 * Created by: Jason A Leger
 * Student ID: 3169026
 * Date : October through Nov 17th 2023
 *
 * Special thanks to Steve for helping me understand the event time logic better
 *
 * opening Comments:  Please see my TME3 Log to understand my process of going through
 * Steps 1 through 4 of the TME Requirements
 *
 * This assignment was very difficult but rewarding for understanding and implementing
 * Complex requirements. I have restarted the assignment from scratch at the very least
 * 10 seperate times. If you have any questions please let me know, The final product is
 * polished for the outputs as I was able to add some flavor to it from each redo.
 *
 * Please see TME3 Test plan to see what I have done to check if the assignment met the
 *
 * Requirements
 *
 * Compile Instrs:  Please ensure tme4.GreenhouseControls.java and tme4.Restore.java are in the parent
 * directory of the latter classes.  If there is any issues please check the import statements.
 *
 *
 */
public interface Fixable { //Added Step4 Part1
    void fix();
    void log();
}

