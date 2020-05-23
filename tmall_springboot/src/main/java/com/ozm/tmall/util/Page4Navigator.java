package com.ozm.tmall.util;
/*先获得jpa提供的分页对象，再对分页对象进行二次封装 ，再根据Pageable传入Page对象中获取结果page*/

import org.springframework.data.domain.Page;

import java.util.List;

//自定义分页器
public class Page4Navigator<T> {
    Page<T>  pageFromJPA;
    //分页超链数 如[5,6,7,8,9] 即分页超链数为5
    int navigatePages;
    //总页面数
    int totalPages;
    //当前处于第几页
    int number;
    //总共有多少条数据
    long totalElements;
    //一页最多有多少条数据
    int size;
    //当前页有多少条数据
    int numberOfElements;
    List<T> content;
    boolean isHasContent;
    //是否是首页
    boolean first;
    //是否是末页
    boolean last;
    //是否有上一页
    boolean isHasNext;
    //是否有下一页
    boolean isHasPrevious;
    //前端展示超链数如[5,6,7,8,9]
    int[] navigatePageNums;
    public Page4Navigator() {
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }
    //第一参数为分页对象，第二个参数为一页内最多显示多少项navigate
    public Page4Navigator(Page<T> pageFromJPA,int navigatePages) {
        //获得Page对象
        this.pageFromJPA = pageFromJPA;
        //获得一页要显示的超链数
        this.navigatePages = navigatePages;
        //获得总共页面数
        totalPages = pageFromJPA.getTotalPages();
        //获得当前处于哪一页
        number  = pageFromJPA.getNumber() ;
        //获得一共多少元素数
        totalElements = pageFromJPA.getTotalElements();
        //获得一页最多有多少条数据
        size = pageFromJPA.getSize();
        //获得当前页有多少条元素
        numberOfElements = pageFromJPA.getNumberOfElements();
        //获取数据集合
        content = pageFromJPA.getContent();

        isHasContent = pageFromJPA.hasContent();

        first = pageFromJPA.isFirst();

        last = pageFromJPA.isLast();

        isHasNext = pageFromJPA.hasNext();

        isHasPrevious  = pageFromJPA.hasPrevious();
        //计算超链数，用于分页显示
        calcNavigatepageNums();

    }

    private void calcNavigatepageNums() {
        //用于保存要显示的超链
        int navigatepageNums[];
        //获得总页数
        int totalPages = getTotalPages();
        //当前处于哪一页
        int num = getNumber();
        //当总页数小于或等于导航超链页码数时
        if (totalPages <= navigatePages) {
            navigatepageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                //超链展示从第一页开始
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时  6  3
            //超链长度即为一页超链数长度
            navigatepageNums = new int[navigatePages]; // 3
            int startNum = num - navigatePages / 2; //1 -3/2  0 or 2 -3/2  1
            int endNum = num + navigatePages / 2; // 1 +3/2  2  2+3/2  3

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    //在第一页，所以要显示的超链为[1,2,....navigatePages]
                    //此处为先赋值后++ 假设navigatePages=3 即显示的超链为[1,2,3]
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
        this.navigatePageNums = navigatepageNums;
    }
    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return isHasContent;
    }

    public void setHasContent(boolean isHasContent) {
        this.isHasContent = isHasContent;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return isHasNext;
    }

    public void setHasNext(boolean isHasNext) {
        this.isHasNext = isHasNext;
    }

    public boolean isHasPrevious() {
        return isHasPrevious;
    }

    public void setHasPrevious(boolean isHasPrevious) {
        this.isHasPrevious = isHasPrevious;
    }

    public int[] getNavigatepageNums() {
        return navigatePageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatePageNums = navigatepageNums;
    }


}
