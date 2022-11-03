package dev.rosewood.rosetimber.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TreeBlockSet<BlockType> implements Collection<ITreeBlock<BlockType>> {

    private final ITreeBlock<BlockType> initialLogBlock;
    private final Set<ITreeBlock<BlockType>> logBlocks, leafBlocks;

    public TreeBlockSet() {
        this.initialLogBlock = null;
        this.logBlocks = new HashSet<>();
        this.leafBlocks = new HashSet<>();
    }

    public TreeBlockSet(ITreeBlock<BlockType> initialLogBlock) {
        this.initialLogBlock = initialLogBlock;
        this.logBlocks = new HashSet<>();
        this.leafBlocks = new HashSet<>();

        if (initialLogBlock != null)
            this.logBlocks.add(initialLogBlock);
    }

    /**
     * @return the TreeBlock of the initial topple point
     */
    public ITreeBlock<BlockType> getInitialLogBlock() {
        return this.initialLogBlock;
    }

    /**
     * @return a Set of log TreeBlocks
     */
    public Set<ITreeBlock<BlockType>> getLogBlocks() {
        return Collections.unmodifiableSet(this.logBlocks);
    }

    /**
     * @return a Set of leaf TreeBlocks
     */
    public Set<ITreeBlock<BlockType>> getLeafBlocks() {
        return Collections.unmodifiableSet(this.leafBlocks);
    }

    /**
     * @return a Set of all TreeBlocks
     */
    public Set<ITreeBlock<BlockType>> getAllTreeBlocks() {
        Set<ITreeBlock<BlockType>> treeBlocks = new HashSet<>();
        treeBlocks.addAll(this.logBlocks);
        treeBlocks.addAll(this.leafBlocks);
        return treeBlocks;
    }

    @Override
    public int size() {
        return this.logBlocks.size() + this.leafBlocks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.logBlocks.isEmpty() && this.leafBlocks.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.logBlocks.contains(o) || this.leafBlocks.contains(o);
    }

    @Override
    public Iterator<ITreeBlock<BlockType>> iterator() {
        return this.getAllTreeBlocks().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.getAllTreeBlocks().toArray();
    }

    @Override
    public boolean add(ITreeBlock<BlockType> treeBlock) {
        return switch (treeBlock.getTreeBlockType()) {
            case LOG -> this.logBlocks.add(treeBlock);
            case LEAF -> this.leafBlocks.add(treeBlock);
        };
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof ITreeBlock<?> treeBlock))
            return false;

        return switch (treeBlock.getTreeBlockType()) {
            case LOG -> this.logBlocks.remove(treeBlock);
            case LEAF -> this.leafBlocks.remove(treeBlock);
        };

    }

    @Override
    public boolean addAll(Collection<? extends ITreeBlock<BlockType>> c) {
        boolean allAdded = true;
        for (ITreeBlock<BlockType> o : c) {
            if (!this.add(o)) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    @Override
    public void clear() {
        this.logBlocks.clear();
        this.leafBlocks.clear();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean retainedAll = true;
        for (Object o : c) {
            if (!this.contains(o)) {
                this.remove(o);
            } else {
                retainedAll = false;
            }
        }
        return retainedAll;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removedAll = true;
        for (Object o : c) {
            if (this.contains(o)) {
                this.remove(o);
            } else {
                removedAll = false;
            }
        }
        return removedAll;
    }

    /**
     * Removes all tree blocks of a given type
     *
     * @param treeBlockType The type of tree block to remove
     * @return true any blocks were removed, otherwise false
     */
    public boolean removeAll(TreeBlockType treeBlockType) {
        if (treeBlockType.equals(TreeBlockType.LOG)) {
            boolean removedAny = !this.logBlocks.isEmpty();
            this.logBlocks.clear();
            return removedAny;
        } else if (treeBlockType.equals(TreeBlockType.LEAF)) {
            boolean removedAny = !this.leafBlocks.isEmpty();
            this.leafBlocks.clear();
            return removedAny;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!this.contains(o))
                return false;
        return true;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("TreeBlockSet does not support this operation.");
    }

}
