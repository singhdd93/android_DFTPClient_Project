package com.singhdd.dftpclient.resuable;

import java.util.Comparator;

/**
 * Created by damandeep on 6/3/15.
 */
public class FileSorter implements Comparator<FileItem> {

    private int sortBasis = Globals.SORT_BY_NAME;

    public FileSorter(int sortBasis) {
        this.sortBasis = sortBasis;
    }

    /**
     * Compares the two specified objects to determine their relative ordering. The ordering
     * implied by the return value of this method for all possible pairs of
     * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
     * This means that
     * <ul>
     * <li>{@code compare(a, a)} returns zero for all {@code a}</li>
     * <li>the sign of {@code compare(a, b)} must be the opposite of the sign of {@code
     * compare(b, a)} for all pairs of (a,b)</li>
     * <li>From {@code compare(a, b) > 0} and {@code compare(b, c) > 0} it must
     * follow {@code compare(a, c) > 0} for all possible combinations of {@code
     * (a, b, c)}</li>
     * </ul>
     *
     * @param lhs an {@code Object}.
     * @param rhs a second {@code Object} to compare with {@code lhs}.
     * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
     * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
     * @throws ClassCastException if objects are not of the correct type.
     */
    @Override
    public int compare(FileItem lhs, FileItem rhs) {

        if(lhs.getType()==Globals.FILE_TYPE_DIRECTORY && !(rhs.getType()==Globals.FILE_TYPE_DIRECTORY))
        {
            return -1;
        }
        else if(!(lhs.getType()==Globals.FILE_TYPE_DIRECTORY) && rhs.getType()==Globals.FILE_TYPE_DIRECTORY)
        {
            return 1;
        }
        else if(sortBasis == Globals.SORT_BY_NAME) {
            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
        }
        else if (sortBasis == Globals.SORT_BY_DATE) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
        else if (sortBasis == Globals.SORT_BY_SIZE) {
            return (lhs.getData() < rhs.getData() ) ? -1: (lhs.getData() > rhs.getData() ) ? 1:0 ;
        }
        else if (sortBasis == Globals.SORT_BY_TYPE) {
            return (lhs.getType() < rhs.getType() ) ? -1: (lhs.getType() > rhs.getType() ) ? 1:0 ;
        }
        else {
            throw new IllegalStateException();
        }
    }
}

