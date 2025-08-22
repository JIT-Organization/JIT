'use client';

import { cn } from '@/lib/utils';
import { useEffect, useRef, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { X } from 'lucide-react';
import Image from 'next/image';

const ImageUploader = ({
  multiple = false,
  onChange,
  defaultImages = [],
  className = ''
}) => {
  const [images, setImages] = useState(defaultImages);
  const [mainIndex, setMainIndex] = useState(0);
  const inputRef = useRef();

  useEffect(() => {
    if (multiple) {
      const result = images.map((img, index) => ({
        file: img.file,
        preview: img.preview,
        isMain: index === mainIndex
      }));
      onChange?.(result);
    } else {
      const singleImage = images[0]
        ? { file: images[0].file, preview: images[0].preview }
        : null;
      onChange?.(singleImage);
    }
  }, [images, mainIndex]);

  const handleUpload = (e) => {
    const files = Array.from(e.target.files);
    const newImages = files.map((file) => ({
      file,
      preview: URL.createObjectURL(file),
    }));

    if (multiple) {
      setImages((prev) => [...prev, ...newImages]);
    } else {
      setImages(newImages.slice(0, 1));
      setMainIndex(0);
    }
  };

  const removeImage = (index) => {
    const updated = images.filter((_, i) => i !== index);
    setImages(updated);
    if (index === mainIndex) {
      setMainIndex(0);
    } else if (index < mainIndex) {
      setMainIndex((prev) => prev - 1);
    }
  };

  const selectMain = (index) => {
    setMainIndex(index);
  };

  return (
    <div className={cn('space-y-4', className)}>
      <input
        type="file"
        multiple={multiple}
        accept="image/*"
        onChange={handleUpload}
        ref={inputRef}
        className="hidden"
      />
      <Button onClick={() => inputRef.current.click()} variant="default">
        Upload {multiple ? 'Images' : 'Image'}
      </Button>

      {images.length > 0 && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {images.map((img, index) => (
            <Card key={index} className="relative group overflow-hidden p-0">
              <Image
                src={img.preview}
                alt={`preview-${index}`}
                className="w-full h-32 object-cover"
              />
              {multiple && (
                <Button
                  onClick={() => selectMain(index)}
                  className={`absolute top-2 left-2 text-xs px-2 py-1 ${index === mainIndex ? "button-primary" : "button button-white"}`}
                >
                  {index === mainIndex ? 'Main' : 'Set Main'}
                </Button>
              )}
              <Button
                onClick={() => removeImage(index)}
                size="icon"
                variant="destructive"
                className="absolute top-2 right-2 w-6 h-6 rounded-full p-0 opacity-80 group-hover:opacity-100 button-remove"
              >
                <X className="w-4 h-4" />
              </Button>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default ImageUploader;
