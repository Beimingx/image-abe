function [x1,y1,w,h]=LabelBox(filename)   %ʹ������עͼ��λ�ò��������꣨��עͼ��ROI�� ��ͼ���й�ѡ���򣬽��
%��ʾ����������в�������ans�С�����ֵx1��y1Ϊ�ؼ��������Ͻ�����ֵ��w,hΪ�ؼ�����Ŀ�͸ߡ�

Mat=imread(filename);
imshow(Mat);
mouse=drawrectangle;
pos=mouse.Position;
%pos=getPosition(mouse);% x1 y1 w h
%ROI=[fix(pos(1)) fix(pos(2)) fix(pos(1)+pos(3)) fix(pos(2)+pos(4))];
x1 = fix(pos(1));
y1= fix(pos(2));
width = fix(pos(3));
height = fix(pos(4));
w = width - mod(width,3);
h =  height - mod(height ,3);
end