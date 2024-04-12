package xyz.chthollywn.cnovel.common.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenzhipeng
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    /**
     * 数据
     */
    private List<T> records;
    /**
     * 当前页码
     */
    private Long current;
    /**
     * 每页显示数据量
     */
    private Long size;
    /**
     * 总页数
     */
    private Long pages;
    /**
     * 数据总数
     */
    private Long total;

    public PageDTO(List<T> records, Long current, Long size, Long total) {
        this.records = records;
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = (long) (int) Math.ceil((double) this.total / this.size);
    }

    public PageDTO(List<T> records, Integer current, Integer size, Integer total) {
        this.records = records;
        this.current = Long.valueOf(current);
        this.size = Long.valueOf(size);
        this.total = Long.valueOf(total);
        this.pages = (long) (int) Math.ceil((double) this.total / this.size);
    }
}
