package org.example.book.enums;

public enum BorrowStatusEnum {
    PENDING("待审核"),
    APPROVED("已批准"),
    BORROWING("借阅中"),
    RETURNED("已归还"),
    REJECTED("已拒绝"),
    ARCHIVED("已归档");

    private final String desc;

    BorrowStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}