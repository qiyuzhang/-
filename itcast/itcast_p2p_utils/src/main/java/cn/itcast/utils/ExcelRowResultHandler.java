package cn.itcast.utils;

import java.util.List;

public interface ExcelRowResultHandler<T> {

	public T invoke(List<Object> list);
}
