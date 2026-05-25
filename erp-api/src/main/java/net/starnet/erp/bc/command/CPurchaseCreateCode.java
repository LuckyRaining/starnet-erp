package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 创建新的编号
 */
@Command
public class CPurchaseCreateCode extends BaseCommand {

    // 将当前日期格式化为 -> 17位的时间戳
    private static SimpleDateFormat codeFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static Random random = new Random();

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // PL + 17位时间戳 + 2位随机数(0-9 + 0-9)
        String code = "PL" + codeFormatter.format(new Date()) + random.nextInt(10) + random.nextInt(10);

        data.put("code", code);
    }

}
