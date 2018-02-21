class ColorDescriptionV2 {
    private String colorCode;
    private String colorDescription;

    public ColorDescriptionV2(String colorCode, String colorDescription) {
        this.colorCode = colorCode;
        this.colorDescription = colorDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorDescriptionV2 colorDescription = (ColorDescriptionV2) o;
        return colorCode.equals(colorDescription.colorCode);
    }

    @Override
    public int hashCode() {
        return colorCode.hashCode();
    }

    @Override
    public String toString() {
        return colorCode + " for " + colorDescription;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getColorDescription() {
        return colorDescription;
    }
}
