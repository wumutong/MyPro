package MyMath;
/**
 * 二叉树
 */
public class BinaryTree {
    private Node root;

    //添加节点
    public void add(int data){
        if(root == null){
            root = new Node(data);
        }else{
            root.addNode(data);
        }
    }

//设置二叉树属性
    class Node{
            private int data; //数据
            private Node left;//左
            private Node right;//右

            public Node(int data){
                this.data=data;
            }
            //添加节点
        public void addNode(int data){
                if(this.data>data){
                    if(this.left == null){
                        this.left=new Node(data);
                    }else{
                        //递归判断左子树上节点
                        this.left.addNode(data);
                    }
                }else {
                    if(this.right == null){
                        this.right=new Node(data);
                    }else{
                        this.right.addNode(data);
                    }
                }
        }
        //递归输出
        public void print(){
                if(this.left !=null){
                    this.left.print();
                }
            System.out.print(this.data+"->");
                if(this.right != null){
                    this.right.print();
                }
        }
        public void printBinaryTree(){
                root.print();
        }
    }

    public static void main(String[] args) {
        BinaryTree bt = new BinaryTree();
        bt.add(8);
        bt.add(22);
        bt.add(1);
        bt.add(83);
        bt.add(833);
        bt.add(2);
        bt.add(5);
        bt.root.printBinaryTree();
    }
}
